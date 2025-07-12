package fr.inventory.packaging.service.core;

import fr.inventory.packaging.entity.dto.InventoryDto;
import fr.inventory.packaging.exceptions.inventory.NoInventoryInProgress;

/**
 * Core service interface for handling inventory operations within the application.
 * Provides methods to access inventory-related business logic.
 */
public interface InventoryService {

    /**
     * Retrieves the currently active inventory.
     *
     * @return the {@link InventoryDto} representing the inventory in progress
     * @throws NoInventoryInProgress if no inventory process is currently active
     */
    InventoryDto getInProgressInventory() throws NoInventoryInProgress;
}
