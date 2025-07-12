package fr.inventory.packaging.exceptions.entity;

/**
 * Exception thrown when an illegal or invalid parameter is encountered.
 * This class extends the {@link EntityException} class.
 *
 */
public class EntityIllegalParameter extends EntityException {

    public EntityIllegalParameter(String message) {
        super(message);
    }
}
