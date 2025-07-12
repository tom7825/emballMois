package fr.inventory.packaging.service.core.impl;

import fr.inventory.packaging.entity.PackagingReference;
import fr.inventory.packaging.entity.StockRegistration;
import fr.inventory.packaging.entity.dto.InventoryDto;
import fr.inventory.packaging.entity.dto.StockRegistrationDto;
import fr.inventory.packaging.entity.dto.StockRegistrationForReportDto;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.entity.reference.ReferenceNotFound;
import fr.inventory.packaging.exceptions.inventory.NoInventoryInProgress;
import fr.inventory.packaging.exceptions.registration.StockRegistrationsNotFound;
import fr.inventory.packaging.repository.StockRegistrationRepository;
import fr.inventory.packaging.service.core.InventoryService;
import fr.inventory.packaging.service.core.PackagingReferenceService;
import fr.inventory.packaging.service.core.StockRegistrationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockRegistrationServiceImpl implements StockRegistrationService {

    private static final Logger logger = LogManager.getLogger();

    private StockRegistrationRepository stockRegistrationRepository;

    private PackagingReferenceService packagingReferenceService;

    private InventoryService inventoryService;

    @Override
    public List<StockRegistrationDto> getRegistrationsDtoFromInventory(Long idInventory) throws EntityIllegalParameter, StockRegistrationsNotFound {
        if(idInventory == null){
            throw new EntityIllegalParameter("Impossible retrouver les enregistrements de stock, id de l'inventaire vide");
        }

        List<StockRegistrationDto> registrationsDto = stockRegistrationRepository.findDtoByInventory_IdInventory(idInventory);
        if(registrationsDto.isEmpty()) {
            throw new StockRegistrationsNotFound();
        }
        logger.info("Get registration from inventory id : {} stock registrations founded", registrationsDto.size());
        return registrationsDto;
    }

    @Override
    public List<StockRegistrationForReportDto> getRegistrationsDtoForReportFromInventory(Long idInventory) throws EntityIllegalParameter, StockRegistrationsNotFound {
        if(idInventory == null){
            throw new EntityIllegalParameter("Impossible retrouver les enregistrements de stock, id de l'inventaire vide");
        }
        List<StockRegistrationForReportDto> registrationsDto = stockRegistrationRepository.findDtoForReportByInventory_IdInventory(idInventory);
        if(registrationsDto.isEmpty()) {
            throw new StockRegistrationsNotFound();
        }
        logger.info("Get registration for report from inventory id : {} stock registrations founded", registrationsDto.size());
        return registrationsDto;
    }

    @Override
    public void updateReferenceInStockRegistration(String referenceName){
        logger.info("Update reference in stock Registration : start");
        if(referenceName == null){
            logger.error("Update reference in stock Registration : error reference name is null");
            return;
        }
        try{
             InventoryDto inventory = inventoryService.getInProgressInventory();
             List<StockRegistration> stockRegistrations = stockRegistrationRepository.getRegistrationsForInventoryInProgressAndReference(referenceName, inventory.getIdInventory());
             updateStockRegistrations(stockRegistrations, referenceName);
             logger.info("Update reference in stock Registration : success");
        }catch (NoInventoryInProgress e){
            logger.warn("Update reference in stock Registration : no inventory in progress, no registration to update");
        }

    }

    private void updateStockRegistrations(List<StockRegistration> stockRegistrations, String referenceName) {
        if(stockRegistrations == null || stockRegistrations.isEmpty()){
            logger.warn("no stock registration to update");
            return;
        }
        try {
            PackagingReference packagingReference = packagingReferenceService.findActiveReferenceByName(referenceName);
            for (StockRegistration stockRegistration : stockRegistrations) {
                stockRegistration.setReference(packagingReference);
                stockRegistrationRepository.save(stockRegistration);
            }
            logger.info("stock registrations updated with new reference");
        } catch (ReferenceNotFound e) {
            logger.warn("Cannot find packaging Reference with name : {}", referenceName);
        }

    }

    @Autowired
    public void setStockRegistrationRepository(StockRegistrationRepository stockRegistrationRepository) {
        this.stockRegistrationRepository = stockRegistrationRepository;
    }

    @Autowired
    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Autowired
    public void setPackagingReferenceService(PackagingReferenceService packagingReferenceService) {
        this.packagingReferenceService = packagingReferenceService;
    }
}
