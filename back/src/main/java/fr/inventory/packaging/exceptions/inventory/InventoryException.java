package fr.inventory.packaging.exceptions.inventory;

/**
 * Base class for exceptions related to inventory operations.
 * This class serves as a foundation for other specific exceptions related to inventory management.
 */
public abstract class InventoryException extends Exception {
    public InventoryException(String message) {
        super(message);
    }
}
