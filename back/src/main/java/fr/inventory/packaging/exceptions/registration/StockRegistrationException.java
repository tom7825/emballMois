package fr.inventory.packaging.exceptions.registration;

/**
 * Abstract base class for all exceptions related to stock registration operations.
 * This class serves as a foundation for specific stock registration exceptions
 */
public abstract class StockRegistrationException extends Exception {
    public StockRegistrationException(String message) {
        super(message);
    }
}
