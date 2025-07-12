package fr.inventory.packaging.service.core;

import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.entity.reference.ReferenceNotFound;

/**
 * Service implementation responsible for updating a packaging reference by associating it with a new storage area.
 * This class handles the archival status of the previous reference and creation of a new updated one.
 */
public interface UpdateReferenceService {

    /**
     * Updates a packaging reference by replacing an associated storage area with a new one.
     * The existing reference is archived and a new copy is created and saved.
     *
     * @param referenceName   the name of the packaging reference to update
     * @param storageAreaName the name of the current storage area to be added
     * @param newId           the ID of the new storage area to associate with the reference
     * @throws EntityIllegalParameter if any input parameter is invalid or if the new area cannot be found
     * @throws ReferenceNotFound      if the reference to be updated does not exist
     */
    void updateReference(String referenceName, String storageAreaName, Long newId) throws EntityIllegalParameter, ReferenceNotFound;

}
