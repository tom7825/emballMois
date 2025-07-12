package fr.inventory.packaging.service.core.impl;

import fr.inventory.packaging.entity.Inventory;
import fr.inventory.packaging.entity.dto.InventoryDto;
import fr.inventory.packaging.exceptions.inventory.NoInventoryInProgress;
import fr.inventory.packaging.repository.InventoryRepository;
import fr.inventory.packaging.service.core.InventoryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImpl implements InventoryService {

    private InventoryRepository inventoryRepository;

    private static final Logger logger = LogManager.getLogger();

    @Override
    public InventoryDto getInProgressInventory() throws NoInventoryInProgress {
        Inventory inventoryInProgress = inventoryRepository.findByEndDateInventoryIsNull();
        if(inventoryInProgress == null){
            logger.warn("Inventory in progress: none found");
            throw new NoInventoryInProgress("Aucun inventaire en cours");
        }
        logger.info("Inventory in progress found, ID={}", inventoryInProgress.getIdInventory());
        return new InventoryDto(inventoryInProgress.getIdInventory(),inventoryInProgress.getStartDateInventory(), inventoryInProgress.getEndDateInventory());
    }

    @Autowired
    public void setInventoryRepository(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }
}
