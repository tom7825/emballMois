package fr.inventory.packaging.service.core;

import fr.inventory.packaging.entity.StorageArea;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.entity.storage.area.AreaNotFound;

/**
 * Service interface for managing storage area operations.
 * Provides methods to retrieve storage area entities by name, including archived ones.
 */
public interface StorageAreaService {

    /**
     * Retrieves any area by its name (active or archived).
     *
     * @param area the name of the area
     * @return the {@link StorageArea} entity matching the given name
     * @throws EntityIllegalParameter if the input is invalid
     * @throws AreaNotFound           if no area with the specified name is found
     */
    StorageArea getLastModificationAreaByName(String area) throws EntityIllegalParameter, AreaNotFound;
}
