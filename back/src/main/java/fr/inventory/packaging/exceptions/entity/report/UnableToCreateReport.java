package fr.inventory.packaging.exceptions.entity.report;

/**
 * Exception thrown when an error occurs while attempting to create a report.
 * This exception is used as a generic handler for cases where report creation fails,
 * either due to an underlying cause or a specific error message provided by the caller.
 */
public class UnableToCreateReport extends Exception {
    public UnableToCreateReport(Exception exception) {
        super(exception.getMessage() + " : impossible de créer un rapport.");
    }

    public UnableToCreateReport(String erreur) {
        super(erreur);
    }
}
