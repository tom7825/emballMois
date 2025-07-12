package fr.inventory.packaging.service.api.impl;

import fr.inventory.packaging.entity.Inventory;
import fr.inventory.packaging.entity.dto.InventoryDto;
import fr.inventory.packaging.entity.dto.response.ApiResponse;
import fr.inventory.packaging.exceptions.inventory.InventoryAlreadyExists;
import fr.inventory.packaging.exceptions.inventory.NoInventoryFound;
import fr.inventory.packaging.exceptions.inventory.NoInventoryInProgress;
import fr.inventory.packaging.repository.InventoryRepository;
import fr.inventory.packaging.service.api.InventoryApiService;
import fr.inventory.packaging.service.core.PackagingReferenceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class InventoryApiServiceImpl implements InventoryApiService {

    private static final Logger logger = LogManager.getLogger();

    private InventoryRepository inventoryRepository;

    private PackagingReferenceService packagingReferenceService;

    @Override
    public void createInventory() throws InventoryAlreadyExists {
        logger.info("Create Inventory : start");
        Inventory inventory = inventoryRepository.findByEndDateInventoryIsNull();
        if (inventoryExistsInProgress()) {
            logger.warn("Create Inventory: inventory already in progress");
            throw new InventoryAlreadyExists("Un inventaire précédent n'a pas été clôturé",inventory);
        }

        Inventory newInventory = new Inventory();
        inventoryRepository.save(newInventory);
        packagingReferenceService.updateReferencesInformationFromExternalDB();

        logger.info("Create Inventory: success, ID={}", newInventory.getIdInventory());
    }

    @Override
    public void endInventory() throws NoInventoryInProgress {
        logger.info("End Inventory : start");
        Inventory inventory = inventoryRepository.findByEndDateInventoryIsNull();
        if (inventory == null) {
            logger.info("End Inventory : no inventory in progress");
            throw new NoInventoryInProgress();
        }
        inventory.closeInventory();
        inventoryRepository.save(inventory);
        logger.info("End Inventory: success, ID={}", inventory.getIdInventory());
    }

    @Override
    public ApiResponse<InventoryDto> inventoryInProgress() throws NoInventoryInProgress {
        Inventory inventoryInProgress = inventoryRepository.findByEndDateInventoryIsNull();
        if(inventoryInProgress == null){
            logger.warn("Inventory in progress: none found");
            throw new NoInventoryInProgress("Aucun inventaire en cours");
        }
        logger.info("Inventory in progress found, ID={}", inventoryInProgress.getIdInventory());
        return new ApiResponse<>("Inventaire trouvé",
                new InventoryDto(inventoryInProgress.getIdInventory(),inventoryInProgress.getStartDateInventory(), null));
    }

    @Override
    public ApiResponse<List<InventoryDto>> getAllCloseInventories() throws NoInventoryFound {
        List<InventoryDto> inventories = inventoryRepository.findAllByEndDateInventoryIsNotNullOrderByEndDateInventoryDesc();
        if(inventories.isEmpty()){
            logger.warn("Get Inventories: no inventories found");
            throw new NoInventoryFound();
        }
        logger.info("Get Inventories: {} found", inventories.size());
        return new ApiResponse<>(inventories.size() + " inventaires trouvés.", inventories);
    }

    private boolean inventoryExistsInProgress() {
        return inventoryRepository.findByEndDateInventoryIsNull() != null;
    }

    @Autowired
    public void setInventoryRepository(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Autowired
    public void setPackagingReferenceService(PackagingReferenceService packagingReferenceService) {
        this.packagingReferenceService = packagingReferenceService;
    }
}
