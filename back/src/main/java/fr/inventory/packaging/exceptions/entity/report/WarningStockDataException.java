package fr.inventory.packaging.exceptions.entity.report;

/**
 * Exception thrown when there is a warning in the stock data, indicating that
 * the data might be incomplete, incorrect, or problematic, but not necessarily
 * severe enough to halt the processing.
 * This exception is used to signal non-critical issues that require attention,
 * but do not block the normal flow of report generation.
 */
public class WarningStockDataException extends ReportException {
    public WarningStockDataException(String message) {
        super(message);
    }
}
