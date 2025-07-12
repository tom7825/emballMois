package fr.inventory.packaging.exceptions.inventory;

import fr.inventory.packaging.exceptions.entity.EntityNotFound;
import fr.inventory.packaging.exceptions.entity.Error;

/**
 * Exception thrown when no inventory is found.
 */
public class NoInventoryFound extends EntityNotFound {
    public NoInventoryFound() {
        super("inventaire",null, Error.ENTITY_NOT_FOUND);
    }
}
