package fr.inventory.packaging.repository;

import fr.inventory.packaging.entity.Inventory;
import fr.inventory.packaging.entity.dto.InventoryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository interface for managing {@link Inventory} entities.
 **/
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    /**
     * Retrieves the active inventory (where the end date is null).
     *
     * @return the active inventory, or null if no active inventory exists
     */
    Inventory findByEndDateInventoryIsNull();

    /**
     * Retrieves all completed inventories (where the end date is not null), ordered by their end date in descending order.
     *
     * @return a list of completed inventories, ordered by end date in descending order
     */
    @Query("SELECT new fr.inventory.packaging.entity.dto.InventoryDto(i.idInventory, i.startDateInventory, i.endDateInventory) FROM Inventory i WHERE i.endDateInventory IS NOT NULL ORDER BY i.endDateInventory")
    List<InventoryDto> findAllByEndDateInventoryIsNotNullOrderByEndDateInventoryDesc();

    /**
     * Retrieves the two most recently finalized inventories,
     * ordered by end date in descending order.
     *
     * @return a list containing the two most recent finalized inventories
     */
    List<Inventory> findTop2ByEndDateInventoryNotNullOrderByEndDateInventoryDesc();

    /**
     * Retrieves the most recently finalized inventories,
     *
     * @return the most recent finalized inventories
     */
    Inventory findTop1ByEndDateInventoryNotNullOrderByEndDateInventoryDesc();
}
