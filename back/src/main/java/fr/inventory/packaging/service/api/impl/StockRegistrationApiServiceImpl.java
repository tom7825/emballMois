package fr.inventory.packaging.service.api.impl;

import fr.inventory.packaging.entity.Inventory;
import fr.inventory.packaging.entity.PackagingReference;
import fr.inventory.packaging.entity.StockRegistration;
import fr.inventory.packaging.entity.StorageArea;
import fr.inventory.packaging.entity.dto.StockRegistrationDto;
import fr.inventory.packaging.entity.dto.response.ApiResponse;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.entity.reference.ReferenceNotFound;
import fr.inventory.packaging.exceptions.entity.storage.area.AreaNotFound;
import fr.inventory.packaging.exceptions.inventory.NoInventoryInProgress;
import fr.inventory.packaging.exceptions.registration.StockRegistrationsNotFound;
import fr.inventory.packaging.exceptions.registration.IncompleteStockRegistrationException;
import fr.inventory.packaging.repository.InventoryRepository;
import fr.inventory.packaging.repository.StockRegistrationRepository;
import fr.inventory.packaging.service.core.PackagingReferenceService;
import fr.inventory.packaging.service.validation.RegistrationValidator;
import fr.inventory.packaging.service.core.StorageAreaService;
import fr.inventory.packaging.service.api.StockRegistrationApiService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockRegistrationApiServiceImpl implements StockRegistrationApiService {

    private static final Logger logger = LogManager.getLogger();

    private StockRegistrationRepository stockRegistrationRepository;

    private InventoryRepository inventoryRepository;

    private PackagingReferenceService packagingReferenceService;

    private StorageAreaService storageAreaService;

    private RegistrationValidator registrationValidator;

    @Override
    public ApiResponse<StockRegistrationDto> addStockRegistration(StockRegistrationDto stockRegistrationDto)
            throws NoInventoryInProgress, EntityIllegalParameter, IncompleteStockRegistrationException {
        logger.info("Stock registration add : start");
        Inventory inventory = inventoryRepository.findByEndDateInventoryIsNull();
        if (inventory == null) {
            logger.error("Stock registration add  : no inventory in progress");
            throw new NoInventoryInProgress();
        }
        if (stockRegistrationDto == null) {
            logger.error("Stock registration add  : empty stock registration data");
            throw new EntityIllegalParameter("Les données de la saisie de stock sont vides");
        }
        try {
            logger.info("Test quantity : {}", stockRegistrationDto.getQuantity());
            PackagingReference packagingReference = packagingReferenceService.
                    findActiveReferenceByName(stockRegistrationDto.getReferenceName());
            StorageArea storageArea = storageAreaService.
                    getLastModificationAreaByName(stockRegistrationDto.getStorageAreaName());
            StockRegistration stockRegistration = new StockRegistration(
                    stockRegistrationDto.getQuantity(),
                    stockRegistrationDto.getComment(),
                    packagingReference,
                    stockRegistrationDto.getPackagingCount());
            stockRegistration.setInventory(inventory);
            stockRegistration.setReference(packagingReference);
            stockRegistration.setStorageArea(storageArea);
            registrationValidator.validateRegistrationForAddOrModify(stockRegistration, packagingReference);
            stockRegistration = stockRegistrationRepository.save(stockRegistration);
            logger.info("Stock registration add  : success");
            return new ApiResponse<>(
                    "Enregistrement de stock pour " + packagingReference.getReferenceName() + " sauvegardé dans " + storageArea.getStorageAreaName(),
                    entityToDto(stockRegistration));
        } catch (ReferenceNotFound e) {
            logger.error("Stock registration add : reference not found");
            throw new EntityIllegalParameter(e.getMessage());
        } catch (AreaNotFound e) {
            logger.error("Stock registration add : storage area not found");
            throw new EntityIllegalParameter(e.getMessage());
        }
    }



    @Override
    public ApiResponse<List<StockRegistrationDto>> getRegistrationsDtoFromInventory(Long idInventory) throws StockRegistrationsNotFound, EntityIllegalParameter {
        if(idInventory == null){
            throw new EntityIllegalParameter("Impossible retrouver les enregistrements de stock, id de l'inventaire vide");
        }
        List<StockRegistrationDto> registrationsDto = stockRegistrationRepository.findDtoByInventory_IdInventory(idInventory);
        if(registrationsDto.isEmpty()) {
            throw new StockRegistrationsNotFound();
        }
        return new ApiResponse<>(registrationsDto.size() + " enregistrement(s) de stock trouvés", registrationsDto);
    }

    @Override
    public ApiResponse<List<StockRegistrationDto>> getRegistrationsValidationFromInventory(Long idInventory) throws EntityIllegalParameter, StockRegistrationsNotFound {
        logger.info("Get registration DTO for validation : start");
        if (idInventory == null) {
            logger.warn("Get registration DTO for validation : stop id Inventory is null");
            throw new EntityIllegalParameter("Inventaire introuvable, id null");
        }
        List<StockRegistrationDto> registrationsDto = stockRegistrationRepository.findDtoWithAreaNameAndReferenceNameByInventory_IdInventory(idInventory);
        if (registrationsDto == null || registrationsDto.isEmpty()) {
            logger.warn("Get registration DTO for validation : stop, no registration found, id inventory : {}", idInventory);
            throw new StockRegistrationsNotFound();
        }
        logger.info("Get registration DTO for validation : success");
        return new ApiResponse<>(registrationsDto.size() + " enregistrements de stock trouvés", registrationsDto);
    }

    @Override
    public void addLastExistingRegistrationsToCurrentInventory(String areaName, String referenceName) throws StockRegistrationsNotFound {
        logger.info("Copy last existing registration of {} in {} to current inventory : start", referenceName, areaName);
        Long idInventory = stockRegistrationRepository.getInventoryIdOfLastExistingRegistration(areaName, referenceName, PageRequest.of(0, 1)).stream()
                .findFirst()
                .orElse(null);
        if (idInventory == null) {
            logger.warn("Copy last existing registration to current inventory : stop, no existing registration found");
            throw new StockRegistrationsNotFound();
        }
        logger.info("Registration found for {} in inventory with id {}", referenceName, idInventory);
        List<StockRegistration> registrations = stockRegistrationRepository.getLastExistingRegistrations(areaName, referenceName, idInventory);
        logger.info(registrations.size() + " registrations found.");
        for (StockRegistration stockRegistration : registrations) {
            try {
                addStockRegistration(entityToDto(stockRegistration));
            } catch (NoInventoryInProgress | EntityIllegalParameter | IncompleteStockRegistrationException e) {
                throw new StockRegistrationsNotFound();
            }
        }
        if (registrations.isEmpty()) {
            logger.warn("Copy last existing registration to current inventory : stop, no existing registration found");
            throw new StockRegistrationsNotFound();
        }
    }

    @Override
    public void updateStockRegistration(StockRegistrationDto stockRegistrationDto) throws EntityIllegalParameter, StockRegistrationsNotFound, IncompleteStockRegistrationException {
        logger.info("update stock registration : start");
        if (stockRegistrationDto == null || stockRegistrationDto.getStockRegistrationId() == null) {
            logger.warn("update stock registration : stop data missing");
            throw new EntityIllegalParameter("Impossible de modifier cet enregistrement de stock");
        }
        Optional<StockRegistration> stockRegistration = stockRegistrationRepository.findById(stockRegistrationDto.getStockRegistrationId());
        PackagingReference reference;
        //Empty id reference means Reference was updated and must be updated from dataBase
        if (stockRegistrationDto.getIdReference() == null) {
            try {
                reference = packagingReferenceService.findActiveReferenceByName(stockRegistrationDto.getReferenceName());
                if (stockRegistration.isEmpty())
                    throw new EntityIllegalParameter("Impossible de modifier cet enregistrement de stock");
                stockRegistration.get().setReference(reference);
            } catch (ReferenceNotFound e) {
                throw new EntityIllegalParameter(e.getMessage());
            }
        }else{
            reference = packagingReferenceService.findReferenceById(stockRegistrationDto.getIdReference());
        }
        if (stockRegistration.isPresent()) {
            StockRegistration stockRegistrationFound = validateStockRegistration(stockRegistrationDto, stockRegistration.get(), reference);
            stockRegistrationRepository.save(stockRegistrationFound);
            logger.info("update stock registration : success");
        } else {
            logger.warn("update stock registration : stop unable to retrieve stock registration");
            throw new StockRegistrationsNotFound();
        }
    }

    @Override
    public ApiResponse<StockRegistrationDto> getRegistrationsDtoById(Long idRegistration) throws EntityIllegalParameter, StockRegistrationsNotFound {
        logger.info("Get registration by id : start");
        if (idRegistration == null) {
            logger.warn("Get registration by id : stop id null");
            throw new EntityIllegalParameter("Impossible de retrouver l'enregistrement, id null");
        }
        Optional<StockRegistration> registration = stockRegistrationRepository.findById(idRegistration);
        if (registration.isEmpty()) {
            logger.warn("Get registration by id : stop registration not found");
            throw new StockRegistrationsNotFound(idRegistration);
        }
        logger.warn("Get registration by id : success");
        return new ApiResponse<>("Enregistrement de stock trouvé.",new StockRegistrationDto(registration.get()));
    }

    @Override
    public void addStockRegistrationWithoutVerification(StockRegistrationDto stockRegistrationDto) throws NoInventoryInProgress, EntityIllegalParameter {
        logger.info("Stock registration add without verification : start");
        Inventory inventory = inventoryRepository.findByEndDateInventoryIsNull();
        if (inventory == null) {
            logger.error("Stock registration add without verification : no inventory in progress");
            throw new NoInventoryInProgress();
        }
        if (stockRegistrationDto == null) {
            logger.error("Stock registration add without verification : empty stock registration data");
            throw new EntityIllegalParameter("Les données de la saisie de stock sont vides");
        }
        try {
            PackagingReference packagingReference = packagingReferenceService.findActiveReferenceByName(stockRegistrationDto.getReferenceName());
            StorageArea storageArea = storageAreaService.getLastModificationAreaByName(stockRegistrationDto.getStorageAreaName());
            StockRegistration stockRegistration = new StockRegistration(stockRegistrationDto.getQuantity(), stockRegistrationDto.getComment(), packagingReference, stockRegistrationDto.getPackagingCount());
            stockRegistration.setInventory(inventory);
            stockRegistration.setReference(packagingReference);
            stockRegistration.setStorageArea(storageArea);
            stockRegistrationRepository.save(stockRegistration);
            logger.info("Stock registration add without verification : success");
        } catch (ReferenceNotFound e) {
            logger.error("Stock registration add without verification: reference not found");
            throw new EntityIllegalParameter(e.getMessage());
        } catch (AreaNotFound e) {
            logger.error("Stock registration add without verification : storage area not found");
            throw new EntityIllegalParameter(e.getMessage());
        }
    }

    @Override
    public Integer addLastExistingRegistrationsForAllReferencesToCurrentInventory(String areaName) {
        logger.info("Adding last existing registrations for all references in {}", areaName);
        List<String> references = packagingReferenceService.findAllActiveReferencesNameForAreaNameWithNoRegistrationInActiveInventory(areaName);
        logger.info("{} references found in {} without registration.", references.size(),areaName);
        Integer count = references.size();
        for (String reference : references) {
            try {
                addLastExistingRegistrationsToCurrentInventory(areaName, reference);
            } catch (StockRegistrationsNotFound e) {
                count--;
            }
        }
        logger.info("{} references modified with new registrations", count);
        return count;
    }


    /**
     * Validates and updates fields for a given stock registration.
     */
    private StockRegistration validateStockRegistration(StockRegistrationDto dto, StockRegistration entity, PackagingReference reference) throws IncompleteStockRegistrationException {
        if (dto.getComment() != null) entity.setComment(dto.getComment());
        if (dto.getQuantity() > 0) entity.setQuantity(dto.getQuantity());
        if (dto.getPackagingCount() != null) entity.setPackagingCount(dto.getPackagingCount());

        //Try to validate Registration + reference
        try {
            registrationValidator.validateRegistrationForAddOrModify(entity, reference);
        } catch (IncompleteStockRegistrationException e) {
            //If validation failed, try new validation with the active reference
            PackagingReference activeReference = getActiveReference(reference.getReferenceName());
            if(activeReference != null && !reference.equals(activeReference)) {
                try {
                    registrationValidator.validateRegistrationForAddOrModify(entity, activeReference);
                    //If validation success, modifying reference attached to the active one
                    entity.setReference(activeReference);
                }catch(IncompleteStockRegistrationException ex){
                    logger.warn("Validation with active reference impossible");
                    throw e;
                }
            }else{
                throw e;
            }
        }
        return entity;
    }

    private PackagingReference getActiveReference(String referenceName) {
        try {
            return packagingReferenceService.findActiveReferenceByName(referenceName);
        } catch (ReferenceNotFound e) {
            return null;
        }
    }


    private StockRegistrationDto entityToDto(StockRegistration stockRegistration) {
        return new StockRegistrationDto(
                stockRegistration.getIdStockRegistration(),
                stockRegistration.getQuantity(),
                stockRegistration.getComment(),
                stockRegistration.getReference().getReferenceName(),
                stockRegistration.getReference().getIdReference(),
                stockRegistration.getStorageArea().getStorageAreaName(),
                stockRegistration.getStorageArea().getStorageAreaId(),
                stockRegistration.getReference().getCategory().getCategoryName(),
                stockRegistration.getPackagingCount(),
                stockRegistration.getReference().getUnitByPackaging()
        );
    }

    @Autowired
    public void setStockRegistrationRepository(StockRegistrationRepository stockRegistrationRepository) {
        this.stockRegistrationRepository = stockRegistrationRepository;
    }

    @Autowired
    public void setInventoryRepository(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Autowired
    public void setPackagingReferenceService(PackagingReferenceService packagingReferenceService) {
        this.packagingReferenceService = packagingReferenceService;
    }

    @Autowired
    public void setStorageAreaService(StorageAreaService storageAreaService) {
        this.storageAreaService = storageAreaService;
    }

    @Autowired
    public void setRegistrationValidator(RegistrationValidator registrationValidator) {
        this.registrationValidator = registrationValidator;
    }
}
