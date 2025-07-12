package fr.inventory.packaging.exceptions.entity.report;

/**
 * Abstract base class for exceptions related to report creation or generation.
 * This exception serves as a parent class for more specific report-related exceptions,
 * providing a common structure and message for all such exceptions.
 */
public abstract class ReportException extends Exception {
    public ReportException(String message) {
        super(message);
    }
}
