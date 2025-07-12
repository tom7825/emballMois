package fr.inventory.packaging.exceptions.entity;

/**
 * Abstract exception class that serves as a base for all entity-related exceptions.
 * This class extends the {@link Exception} class and provides a constructor to set the exception message.
 *
 */
public abstract class EntityException extends Exception {

    private String message;

    public EntityException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
