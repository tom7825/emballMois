package fr.inventory.packaging.service.api.impl;

import fr.inventory.packaging.entity.Category;
import fr.inventory.packaging.entity.PackagingReference;
import fr.inventory.packaging.entity.StorageArea;
import fr.inventory.packaging.entity.dto.PackagingReferenceDto;
import fr.inventory.packaging.entity.dto.response.ApiResponse;
import fr.inventory.packaging.exceptions.entity.Error;
import fr.inventory.packaging.exceptions.entity.EntityAlreadyExist;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.entity.storage.area.AreaNotFound;
import fr.inventory.packaging.exceptions.entity.category.CategoryNotFound;
import fr.inventory.packaging.exceptions.entity.reference.ReferenceNotFound;
import fr.inventory.packaging.exceptions.entity.reference.IncompleteReference;
import fr.inventory.packaging.repository.ReferenceRepository;
import fr.inventory.packaging.service.core.CategoryService;
import fr.inventory.packaging.service.api.PackagingReferenceApiService;
import fr.inventory.packaging.service.core.StockRegistrationService;
import fr.inventory.packaging.service.utils.PackagingReferenceMapper;
import fr.inventory.packaging.service.validation.ReferenceValidator;
import fr.inventory.packaging.service.core.StorageAreaService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class PackagingReferenceApiServiceImpl implements PackagingReferenceApiService {

    private static final Logger logger = LogManager.getLogger();

    private ReferenceRepository referenceRepository;

    private StorageAreaService storageAreaService;

    private CategoryService categoryService;

    private ReferenceValidator referenceValidator;

    private StockRegistrationService stockRegistrationService;

    @Override
    public void addReference(PackagingReferenceDto packagingReferenceDto) throws EntityIllegalParameter, EntityAlreadyExist, IncompleteReference {
        logger.info("Add Reference : start");
        if (packagingReferenceDto == null) {
            logger.error("Add Reference : stop, empty data");
            throw new EntityIllegalParameter("Les données de la référence sont vides");
        }
        referenceValidator.validateReference(packagingReferenceDto);
        PackagingReference existingPackingReference = referenceRepository.findByReferenceNameAndModificationDateReferenceIsNull(packagingReferenceDto.getReferenceName());
        if (existingPackingReference == null) {
            try {
                PackagingReference packagingReference = createPackagingReference(packagingReferenceDto);
                logger.info("Add Reference : success");
                referenceRepository.save(packagingReference);
            } catch (EntityIllegalParameter e) {
                logger.error("Add Reference : stop, error during process");
                throw new EntityIllegalParameter(e.getMessage());
            }
        } else {
            logger.error("Add Reference : stop, reference already exists");
            throw new EntityAlreadyExist(existingPackingReference.getReferenceName(), Error.ENTITY_ALREADY_EXISTS_CREATE);
        }
    }

    @Override
    public ApiResponse<List<PackagingReferenceDto>> findAllActiveReference() throws ReferenceNotFound {
        logger.info("Find all active references : start");
        List<PackagingReferenceDto> referencesDto = referenceRepository.findAllByArchivedPackagingFalseAndModificationDateReferenceIsNullOrderByIdReferenceDesc().stream()
                .map(PackagingReferenceDto::new)
                .toList();
        if (referencesDto.isEmpty()) {
            logger.error("Find all active references : references not found");
            throw new ReferenceNotFound(Error.ENTITY_NOT_FOUND_ACTIVE);
        }

        logger.info("Find all active references : success");
        return new ApiResponse<>(referencesDto.size() + " références trouvées", referencesDto);
    }

    @Override
    public ApiResponse<List<PackagingReferenceDto>> getActiveReferencesByAreaName(String areaName) throws ReferenceNotFound, AreaNotFound, EntityIllegalParameter {
        logger.info("Find references by area name : Start");
        if (areaName == null || areaName.isEmpty()) {
            throw new EntityIllegalParameter("Impossible de trouvé la référence, le nom de la zone est manquant.");
        }
        Long id = storageAreaService.getLastModificationAreaByName(areaName).getStorageAreaId();
        List<PackagingReference> references = referenceRepository.findActiveReferencesByAreaIdOrderByIdReferenceDesc(id);
        if (references.isEmpty()) {
            logger.warn("Find references by area name : none found");
            throw new ReferenceNotFound(Error.ENTITY_NOT_FOUND);
        }
        List<PackagingReferenceDto> referencesDto = references.stream()
                .map(ref -> new PackagingReferenceDto(ref.getIdReference(), ref.getReferenceName(), ref.getUnitCount(), ref.getUnitByPackaging()))
                .collect(Collectors.toList());
        logger.info("Find references by area name : success");
        return new ApiResponse<>(referencesDto.size() + " références trouvées", referencesDto);
    }

    @Override
    public void modifyReference(PackagingReferenceDto packagingReferenceDto) throws EntityIllegalParameter, ReferenceNotFound {
        logger.info("Modify Reference : start");
        if (packagingReferenceDto == null) {
            logger.error("Modify Reference : stop, empty data");
            throw new EntityIllegalParameter("Les données de la référence sont vides");
        }
        PackagingReference existingPackingReference = referenceRepository.findByReferenceNameAndModificationDateReferenceIsNull(packagingReferenceDto.getReferenceName());
        if (existingPackingReference == null) {
            logger.error("Modify Reference : stop, reference not found");
            throw new ReferenceNotFound(packagingReferenceDto.getReferenceName(), Error.ENTITY_NOT_FOUND_NAME);
        } else {
            try {
                logger.info("Reference areas : {}", existingPackingReference.getAreas().size());
                PackagingReference modifiableExistingPackingReference = PackagingReferenceMapper.copyOf(existingPackingReference);
                modifiableExistingPackingReference.setIdReference(null);
                modifyPackagingReference(modifiableExistingPackingReference, packagingReferenceDto);
                if (modifiableExistingPackingReference.equals(existingPackingReference) && isSameStorageAreas(modifiableExistingPackingReference.getAreas(),existingPackingReference.getAreas())) {
                    logger.info("Modify Reference : stop, packaging reference already up to date");
                    throw new EntityIllegalParameter("Aucune donée différente, référence inchangée");
                } else {
                    existingPackingReference.setModificationDateReference(LocalDateTime.now());
                    referenceRepository.save(existingPackingReference);
                    modifiableExistingPackingReference.setIdMainReference(existingPackingReference.getIdReference());
                    referenceRepository.save(modifiableExistingPackingReference);
                    logger.info("Modify Reference : success");

                    //Update reference for in progress stockRegistrations
                    stockRegistrationService.updateReferenceInStockRegistration(packagingReferenceDto.getReferenceName());
                }
            } catch (EntityIllegalParameter | IncompleteReference e) {
                logger.error("Modify Reference : stop, error during process");
                throw new EntityIllegalParameter(e.getMessage());
            }
        }
    }

    private boolean isSameStorageAreas(List<StorageArea> areas, List<StorageArea> areas1) {
        Set<Long> ids = areas.stream().map(StorageArea::getStorageAreaId).collect(Collectors.toSet());
        Set<Long> ids1 = areas1.stream().map(StorageArea::getStorageAreaId).collect(Collectors.toSet());
        return ids.equals(ids1);
    }

    @Override
    public ApiResponse<List<PackagingReferenceDto>> findAllArchivedReference() throws ReferenceNotFound {
        logger.info("Find all archived references : start");
        List<PackagingReference> references = referenceRepository.findAllByArchivedPackagingTrueAndModificationDateReferenceIsNullOrderByIdReferenceDesc();
        if (references.isEmpty()) {
            logger.error("Find all archived references : references not found");
            throw new ReferenceNotFound(Error.ENTITY_NOT_FOUND);
        }
        List<PackagingReferenceDto> referencesDto = new ArrayList<>();
        for (PackagingReference reference : references) {
            referencesDto.add(new PackagingReferenceDto(reference));
        }
        logger.info("Find all archived references : success");
        return new ApiResponse<>(referencesDto.size() + " références archivées trouvées", referencesDto);
    }

    @Override
    public ApiResponse<List<PackagingReferenceDto>> findAllReferencesExcludingArea(String areaName) throws EntityIllegalParameter, ReferenceNotFound {
        if (areaName == null || areaName.isEmpty()) {
            throw new EntityIllegalParameter("Impossible de rechercher les références hors de la zone, nom de zone manquant.");
        }
        List<PackagingReferenceDto> referencesDto = new ArrayList<>();
        for (PackagingReference reference : referenceRepository.getReferencesExcludingArea(areaName)) {
            referencesDto.add(new PackagingReferenceDto(reference));
        }
        if (referencesDto.isEmpty()) {
            throw new ReferenceNotFound("Aucune référence hors de la zone " + areaName, Error.ENTITY_NOT_FOUND);
        }
        return new ApiResponse<>(referencesDto.size() + " références trouvées pour la zone " + areaName, referencesDto);
    }

    @Override
    public void addMinimalReference(PackagingReferenceDto packagingReferenceDto) throws EntityIllegalParameter {
        logger.info("Add minimal Reference : start");
        if (packagingReferenceDto == null) {
            logger.error("Add minimal Reference : stop, empty data");
            throw new EntityIllegalParameter("Les données de la référence sont vides");
        }
        PackagingReference existingPackingReference = referenceRepository.findByReferenceNameAndModificationDateReferenceIsNull(packagingReferenceDto.getReferenceName());
        if (existingPackingReference == null) {
                fillMinimalReferenceInformation(packagingReferenceDto);
                PackagingReference packagingReference = PackagingReferenceMapper.fromDto(packagingReferenceDto);
                addAreasToReference(packagingReferenceDto.getAreasName(), packagingReference);
                logger.info("Add minimal Reference : success");
                referenceRepository.save(packagingReference);
        } else {
            logger.error("Add minimal Reference : reference already exists");
        }
    }

    @Override
    public void deleteArea(Long referenceId, String areaName) throws EntityIllegalParameter {
        if(referenceId == null || areaName == null || areaName.isEmpty()){
            logger.debug("Delete area of Reference : stop, invalid data");
            throw new EntityIllegalParameter("Invalid data");
        }
        PackagingReference reference = referenceRepository.findReferenceById(referenceId);
        if(reference == null){
            logger.debug("Delete area of Reference : stop, unable to find reference");
            throw new EntityIllegalParameter("unable to find reference");
        }
        List<StorageArea> areas = reference.getAreas();
        Optional<StorageArea> areaToRemove = areas.stream()
                .filter(area -> areaName.equals(area.getStorageAreaName()))
                .findFirst();

        if (areaToRemove.isPresent()) {
            areas.remove(areaToRemove.get());
            referenceRepository.save(reference);
            logger.debug("Area '{}' removed from reference '{}'", areaName, referenceId);
        } else {
            logger.debug("Delete area of Reference : stop, area '{}' not found in reference '{}'", areaName, referenceId);
            throw new EntityIllegalParameter("Area not associated with reference");
        }

    }

    private void fillMinimalReferenceInformation(PackagingReferenceDto packagingReferenceDto) {
        if(packagingReferenceDto.getArchivedPackaging() == null){
            packagingReferenceDto.setArchivedPackaging(false);
        }
    }

    private PackagingReference createPackagingReference(PackagingReferenceDto packagingReferenceDto) throws EntityIllegalParameter {
        logger.info("Create reference {} : start", packagingReferenceDto.getReferenceName());
        PackagingReference packagingReference = PackagingReferenceMapper.fromDto(packagingReferenceDto);
        logger.info("Create reference {} : add areas", packagingReferenceDto.getReferenceName());
        addAreasToReference(packagingReferenceDto.getAreasName(), packagingReference);
        logger.info("Create reference {} : add category", packagingReferenceDto.getReferenceName());
        addCategoryToReference(packagingReferenceDto.getCategoryName(), packagingReference);
        logger.info("Create reference {} : success", packagingReferenceDto.getReferenceName());
        return packagingReference;
    }

    private void modifyPackagingReference(PackagingReference existingReference, PackagingReferenceDto packagingReferenceDto) throws EntityIllegalParameter, IncompleteReference {
        if (packagingReferenceDto.getUnitByPackaging() != null) {
            existingReference.setUnitByPackaging(packagingReferenceDto.getUnitByPackaging());
        }
        if (packagingReferenceDto.getUnitPrice() != null) {
            existingReference.setUnitPrice(packagingReferenceDto.getUnitPrice());
        }
        if (packagingReferenceDto.getUnitSupplier() != null) {
            existingReference.setUnitSupplier(packagingReferenceDto.getUnitSupplier());
        }
        if(packagingReferenceDto.getUnitCount() != null){
            existingReference.setUnitCount(packagingReferenceDto.getUnitCount());
        }
        if (packagingReferenceDto.getCalculationRule() != null) {
            existingReference.setCalculationRule(packagingReferenceDto.getCalculationRule());
        }
        if (packagingReferenceDto.getSupplierName() != null) {
            existingReference.setSupplierName(packagingReferenceDto.getSupplierName());
        }
        if (packagingReferenceDto.getCategoryName() != null) {
            logger.info("Modify reference {} : add category", packagingReferenceDto.getReferenceName());
            addCategoryToReference(packagingReferenceDto.getCategoryName(), existingReference);
        }
        if(packagingReferenceDto.getReferenceProductionDB() != null){
            existingReference.setReferenceProductionDB(packagingReferenceDto.getReferenceProductionDB());
        }
        addAreasToReference(packagingReferenceDto.getAreasName(), existingReference);
        if(existingReference.isArchivedPackaging() == packagingReferenceDto.getArchivedPackaging()) {
            referenceValidator.validateReference(existingReference);
        }
        if (packagingReferenceDto.getArchivedPackaging() != null) {
            existingReference.setArchivedPackaging(packagingReferenceDto.isArchivedPackaging());
        }
    }

    private void addCategoryToReference(String categoryName, PackagingReference packagingReference) throws EntityIllegalParameter {
        logger.info("Add category {} to reference : start", categoryName);
        try {
            Category category = categoryService.getActiveCategoryByName(categoryName);
            packagingReference.setCategory(category);
            logger.info("Add category to reference : success, category {} added to reference", categoryName);
        } catch (CategoryNotFound e) {
            logger.error("Add category to reference : stop, category {} not found", categoryName);
            throw new EntityIllegalParameter(e.getMessage());
        }

    }

    private void addAreasToReference(List<String> areasName, PackagingReference packagingReference) throws EntityIllegalParameter {
        logger.info("Add area to Reference : start");
        if(areasName.isEmpty()){
            logger.warn("No area passed as a parameter");
            return;
        }
        try {
            List<StorageArea> resolvedAreas = new ArrayList<>();
            for (String areaName : areasName) {
                StorageArea area = storageAreaService.getLastModificationAreaByName(areaName);
                resolvedAreas.add(area);
                logger.info("Add area to Reference : success, adding {}", areaName);
            }

            packagingReference.setAreas(resolvedAreas);
            logger.info("Add area to Reference : success");
        } catch (AreaNotFound e) {
            logger.error("Add area to Reference : stop, area not found");
            throw new EntityIllegalParameter(e.getMessage());
        }
    }



    @Autowired
    public void setReferenceRepository(ReferenceRepository referenceRepository) {
        this.referenceRepository = referenceRepository;
    }

    @Autowired
    public void setStorageAreaService(StorageAreaService storageAreaService) {
        this.storageAreaService = storageAreaService;
    }

    @Autowired
    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Autowired
    public void setReferenceValidator(ReferenceValidator referenceValidator) {
        this.referenceValidator = referenceValidator;
    }

    @Autowired
    public void setStockRegistrationService(StockRegistrationService stockRegistrationService) {
        this.stockRegistrationService = stockRegistrationService;
    }
}
