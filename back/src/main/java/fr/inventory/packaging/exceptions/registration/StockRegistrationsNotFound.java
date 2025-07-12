package fr.inventory.packaging.exceptions.registration;

import fr.inventory.packaging.exceptions.entity.EntityNotFound;
import fr.inventory.packaging.exceptions.entity.Error;

/**
 * Exception thrown when stock registrations are not found.
 * This class extends from {@link EntityNotFound} and represents
 * the case when stock registrations could not be found in the system.
 */
public class StockRegistrationsNotFound extends EntityNotFound {
    public StockRegistrationsNotFound() {
        super("enregistrement de stock",null, Error.ENTITY_NOT_FOUND);
    }

    public StockRegistrationsNotFound(Long id) {
        super("enregistrement de stock",id.toString(), Error.ENTITY_NOT_FOUND_ID);
    }
}
