package fr.inventory.packaging.service.api;

import fr.inventory.packaging.entity.dto.InventoryDto;
import fr.inventory.packaging.entity.dto.response.ApiResponse;
import fr.inventory.packaging.exceptions.inventory.InventoryAlreadyExists;
import fr.inventory.packaging.exceptions.inventory.NoInventoryFound;
import fr.inventory.packaging.exceptions.inventory.NoInventoryInProgress;

import java.util.List;

/**
 * This interface defines operations for creating, ending, retrieving, and listing inventory sessions.
 */
public interface InventoryApiService {

    /**
     * Creates a new inventory session and update data from ProductionDb
     *
     * @throws InventoryAlreadyExists if an inventory session is already in progress
     */
    void createInventory() throws InventoryAlreadyExists;

    /**
     * Ends the currently active inventory session by setting its end date.
     *
     * @throws NoInventoryInProgress if no inventory session is currently active
     */
    void endInventory() throws NoInventoryInProgress;

    /**
     * Retrieves the inventory session currently in progress.
     *
     * @return the {@link InventoryDto} representing the active inventory
     * @throws NoInventoryInProgress if no inventory session is currently active
     */
    ApiResponse<InventoryDto> inventoryInProgress() throws NoInventoryInProgress;

    /**
     * Retrieves all inventory sessions (completed).
     *
     * @return a list of {@link InventoryDto} objects representing all inventory sessions
     * @throws NoInventoryFound if no inventories exist in the system
     */
    ApiResponse<List<InventoryDto>> getAllCloseInventories() throws NoInventoryFound;
}
