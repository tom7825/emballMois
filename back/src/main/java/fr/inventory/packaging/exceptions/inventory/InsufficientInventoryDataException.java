package fr.inventory.packaging.exceptions.inventory;

/**
 * Thrown when there is not enough inventory data to perform forecast calculations.
 */
public class InsufficientInventoryDataException extends InventoryException {
  public InsufficientInventoryDataException(String message) {
    super(message);
  }
}
