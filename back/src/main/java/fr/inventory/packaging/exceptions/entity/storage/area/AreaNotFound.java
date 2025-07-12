package fr.inventory.packaging.exceptions.entity.storage.area;

import fr.inventory.packaging.exceptions.entity.Error;
import fr.inventory.packaging.exceptions.entity.EntityNotFound;

/**
 * Exception thrown when a storage area cannot be found.
 * Extends {@link EntityNotFound} to provide additional context for entity type and identifier.
 */
public class AreaNotFound extends EntityNotFound {
    public AreaNotFound(String areaName,Error error) {
        super("zone de stockage",areaName,error);
    }

    public AreaNotFound() {
        super("zone de stockage");
    }
}
