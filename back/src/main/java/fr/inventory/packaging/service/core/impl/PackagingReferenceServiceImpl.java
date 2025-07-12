package fr.inventory.packaging.service.core.impl;

import fr.inventory.packaging.entity.Inventory;
import fr.inventory.packaging.entity.PackagingReference;
import fr.inventory.packaging.entity.dto.AreaReferenceDto;
import fr.inventory.packaging.entity.dto.validation.LineValidationDto;
import fr.inventory.packaging.entity.external.dto.LastInformationProjection;
import fr.inventory.packaging.exceptions.entity.Error;
import fr.inventory.packaging.exceptions.entity.dbproduction.DataNotFoundInDbProduction;
import fr.inventory.packaging.exceptions.entity.reference.ReferenceNotFound;
import fr.inventory.packaging.repository.InventoryRepository;
import fr.inventory.packaging.repository.ReferenceRepository;
import fr.inventory.packaging.repository.external.CommandeArticleRepository;
import fr.inventory.packaging.service.core.PackagingReferenceService;
import fr.inventory.packaging.service.utils.PackagingReferenceMapper;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PackagingReferenceServiceImpl implements PackagingReferenceService {

    private static final Logger logger = LogManager.getLogger(PackagingReferenceServiceImpl.class);

    private ReferenceRepository referenceRepository;

    private InventoryRepository inventoryRepository;

    private CommandeArticleRepository commandeArticleRepository;

    @Override
    public PackagingReference findActiveReferenceByName(String referenceName) throws ReferenceNotFound {
        logger.info("Find active reference by name : start");
        PackagingReference packagingReference = referenceRepository.findByReferenceNameAndModificationDateReferenceIsNullAndArchivedPackagingFalse(referenceName);
        if (packagingReference == null) {
            logger.error("Find active reference by name : error {} not found", referenceName);
            throw new ReferenceNotFound(referenceName, Error.ENTITY_NOT_FOUND_ACTIVE);
        }
        logger.info("Find active reference by name : success");
        return packagingReference;
    }

    @Override
    public List<AreaReferenceDto> findAllActiveReferencesNameByAreaName() {
        return referenceRepository.findAllActiveReferenceNamesByArea();
    }

    @Override
    public List<String> findAllActiveReferencesNameForAreaNameWithNoRegistrationInActiveInventory(String areaName) {
        Inventory currentInventory = inventoryRepository.findByEndDateInventoryIsNull();
        if(currentInventory == null){
            return Collections.emptyList();
        }
        return referenceRepository.findAllActiveReferenceNamesForAreaWithNoRegistrationInCurrentInventory(areaName, currentInventory.getIdInventory());
    }

    @Override
    public LineValidationDto findReferenceValidationById(Long referenceId) {
        return referenceRepository.findDtoById(referenceId);
    }

    @Override
    public PackagingReference findReferenceById(Long idReference) {
        return referenceRepository.findReferenceById(idReference);
    }

    @Override
    @Async
    @Transactional
    public void updateReferencesInformationFromExternalDB() {
        logger.info("Update References information from production database");
        List<PackagingReference> activeReferences = referenceRepository.findAllByArchivedPackagingFalseAndModificationDateReferenceIsNullOrderByIdReferenceDesc();

        Inventory lastEndedInventory = inventoryRepository.findTop1ByEndDateInventoryNotNullOrderByEndDateInventoryDesc();
        if (lastEndedInventory == null) {
            logger.debug("No previous inventory found");
            return;
        }
        logger.debug("Number of reference to update : {}", activeReferences.size());
        for (PackagingReference activeReference : activeReferences) {
            if (activeReference.getReferenceProductionDB() == null || activeReference.getReferenceProductionDB().isEmpty()) {
                logger.debug("No reference id for production db {}", activeReference.getReferenceName());
                continue;
            }
            try {
                logger.debug("Update References {} ", activeReference.getReferenceName());

                LastInformationProjection lastInformationProjection = getReferenceLastInformationFromProductionDataBase(activeReference.getReferenceProductionDB(), lastEndedInventory.getEndDateInventory());
                logger.info("Last information : Price : {}, Invoice : {}, Supplier : {}", lastInformationProjection.getPrixUnitaire(),
                     lastInformationProjection.getNumPiece(), lastInformationProjection.getLibPro());
                updatePriceAndInvoiceNumber(lastInformationProjection,activeReference);
                updateSupplier(lastInformationProjection,activeReference);
            } catch (DataNotFoundInDbProduction e) {
                logger.debug("No new price found for {}", activeReference.getReferenceName());
            } catch (Exception e) {
                logger.error("An unexpected error has occurred during the reference {} update  : {}", activeReference.getReferenceName(), e);
            }
        }
        logger.info("Update References information from production database finished");
    }

    private void updateSupplier(LastInformationProjection lastInformationProjection, PackagingReference activeReference){
        if (lastInformationProjection.getLibPro() == null ) {
            logger.debug("Update Supplier : Incomplete projection data for reference {}", activeReference.getReferenceName());
        }else {
            if(activeReference.getSupplierName() != null && activeReference.getSupplierName().equals(lastInformationProjection.getLibPro())){
                logger.debug("Update Supplier : Update References information from production database {} : same data no update", activeReference.getReferenceName());
            }else {
                updateReferenceSupplier(activeReference, lastInformationProjection.getLibPro());
                logger.debug("Update Supplier : Update References information from production database {} success", activeReference.getReferenceName());
            }
        }
    }

    private void updatePriceAndInvoiceNumber(LastInformationProjection lastInformationProjection, PackagingReference activeReference){
        if (lastInformationProjection.getPrixUnitaire() == null || lastInformationProjection.getNumPiece() == null) {
            logger.debug("Update Price : Incomplete projection data for reference {}", activeReference.getReferenceName());
        }else {
            Float newPrice = lastInformationProjection.getPrixUnitaire().floatValue();
            Integer refFact = lastInformationProjection.getNumPiece().intValue();

            if (!areFloatsEqual(newPrice, activeReference.getUnitPrice())) {
                updateReferencePrice(activeReference, newPrice, refFact);
                logger.debug("Update References information from production database {} success", activeReference.getReferenceName());
            } else {
                logger.debug("Update References information from production database {} : same data no update", activeReference.getReferenceName());
            }
        }
    }

    private LastInformationProjection getReferenceLastInformationFromProductionDataBase(String idReferenceInProductionDb, LocalDateTime endDateOfTheLastInventory) throws DataNotFoundInDbProduction {
        logger.debug("id : {} et lastModif : {}", idReferenceInProductionDb, endDateOfTheLastInventory);
        Optional<List<LastInformationProjection>> unitInformationProjection = commandeArticleRepository.getLastInformationsBasedOnLastDateLiv(idReferenceInProductionDb, endDateOfTheLastInventory);
        if (unitInformationProjection.isEmpty()) {
            logger.debug("unit information for reference with id {} in production Db not found", idReferenceInProductionDb);
            throw new DataNotFoundInDbProduction("Information introuvable");
        }
        List<LastInformationProjection> informations = unitInformationProjection.get();
        informations.removeIf(info -> info.getPrixUnitaire() == null || info.getPrixUnitaire() == 0);
        if (informations.isEmpty()) {
            logger.debug("unit information for reference with id {} in production Db empty after null prices removed", idReferenceInProductionDb);
            throw new DataNotFoundInDbProduction("Prix introuvable");
        }
        return unitInformationProjection.get().get(0);
    }

    public void updateReferencePrice(PackagingReference packagingReference, Float price, Integer refFact) {
        logger.info("Update Reference : start");
        if (packagingReference == null) {
            logger.error("Update Reference : stop, empty data");
            return;
        }

        PackagingReference modifiableExistingPackingReference = PackagingReferenceMapper.copyOf(packagingReference);
        modifiableExistingPackingReference.setUnitPrice(price);
        modifiableExistingPackingReference.setUnitPriceRefFac(refFact);
        modifiableExistingPackingReference.setIdReference(null);
        packagingReference.setModificationDateReference(LocalDateTime.now());
        referenceRepository.save(packagingReference);
        modifiableExistingPackingReference.setIdMainReference(packagingReference.getIdReference());
        referenceRepository.save(modifiableExistingPackingReference);
        logger.info("Update Reference : success");
    }

    public void updateReferenceSupplier(PackagingReference packagingReference, String supplier) {
        logger.info("Update Reference supplier data : start");
        if (packagingReference == null || supplier == null) {
            logger.error("Update Reference supplier data : stop, empty data");
            return;
        }
        PackagingReference modifiableExistingPackingReference = PackagingReferenceMapper.copyOf(packagingReference);
        modifiableExistingPackingReference.setSupplierName(supplier);
        modifiableExistingPackingReference.setIdReference(null);
        packagingReference.setModificationDateReference(LocalDateTime.now());
        referenceRepository.save(packagingReference);
        modifiableExistingPackingReference.setIdMainReference(packagingReference.getIdReference());
        referenceRepository.save(modifiableExistingPackingReference);
        logger.info("Update Reference supplier data : success");
    }

    private boolean areFloatsEqual(Float a, Float b) {
        if (a == null || b == null) return false;
        return Math.abs(a - b) < 0.0001f;
    }

    @Autowired
    public void setReferenceRepository(ReferenceRepository referenceRepository) {
        this.referenceRepository = referenceRepository;
    }

    @Autowired
    public void setInventoryRepository(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Autowired
    public void setCommandeArticleRepository(CommandeArticleRepository commandeArticleRepository) {
        this.commandeArticleRepository = commandeArticleRepository;
    }
}
