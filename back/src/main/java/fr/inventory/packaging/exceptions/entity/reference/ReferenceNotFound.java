package fr.inventory.packaging.exceptions.entity.reference;

import fr.inventory.packaging.exceptions.entity.Error;
import fr.inventory.packaging.exceptions.entity.EntityNotFound;

/**
 * Exception thrown when a reference is not found.
 * This exception is thrown when an entity of type "reference" cannot be located
 * within the system based on the provided reference name.
 */
public class ReferenceNotFound extends EntityNotFound {
    public ReferenceNotFound(String referenceName, Error error) {
        super("référence",referenceName, error);
    }

    public ReferenceNotFound(Error error){
        super("référence");
    }
}
