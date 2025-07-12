package fr.inventory.packaging.exceptions.inventory;

import fr.inventory.packaging.entity.Inventory;

/**
 * Exception thrown when an attempt is made to create or add an inventory that already exists.
 */
public class InventoryAlreadyExists extends InventoryException {

    private final Inventory existingInventory;

    public InventoryAlreadyExists(String message, Inventory existingInventory) {
        super(message);
        this.existingInventory = existingInventory;
    }

    public Inventory getExistingInventory() {
        return existingInventory;
    }
}
