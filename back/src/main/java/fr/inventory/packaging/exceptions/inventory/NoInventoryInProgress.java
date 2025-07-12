package fr.inventory.packaging.exceptions.inventory;

/**
 * Exception thrown when there is no inventory in progress.
 */
public class NoInventoryInProgress extends InventoryException {
    public NoInventoryInProgress(String message) {
        super(message);
    }

    public NoInventoryInProgress() {
        super("Aucun inventaire en cours");
    }
}
