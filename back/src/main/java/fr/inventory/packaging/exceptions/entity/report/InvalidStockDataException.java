package fr.inventory.packaging.exceptions.entity.report;

/**
 * Exception thrown when there is invalid stock data encountered during report generation or stock registration.
 * This exception extends from {@link UnableToCreateReport} and is used to signal issues
 * related to stock data that prevent the creation of a valid report.
 */
public class InvalidStockDataException extends UnableToCreateReport {
    public InvalidStockDataException(String message) {
        super(message);
    }
}
