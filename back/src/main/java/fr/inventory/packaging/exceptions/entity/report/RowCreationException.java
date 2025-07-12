package fr.inventory.packaging.exceptions.entity.report;

/**
 * Exception thrown when an error occurs during the creation of a row in a report.
 * This exception is specifically used for cases where there is an issue creating
 * or manipulating rows in the report generation process.
 */
public class RowCreationException extends ReportException {
    public RowCreationException(String message) {
        super(message);
    }
}
