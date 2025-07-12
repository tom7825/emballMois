package fr.inventory.packaging.service.api;

import fr.inventory.packaging.entity.StorageArea;
import fr.inventory.packaging.entity.dto.StorageAreaDto;
import fr.inventory.packaging.entity.dto.response.ApiResponse;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.entity.storage.area.AreaAlreadyExists;
import fr.inventory.packaging.exceptions.entity.storage.area.AreaNotFound;

import java.util.List;

/**
 * Service interface for managing {@link fr.inventory.packaging.entity.StorageArea} entities.
 * This interface provides methods to add, modify, and retrieve storage areas,
 * including support for active and archived areas.
 */
public interface StorageAreaApiService {

    /**
     * Adds a new storage area.
     *
     * @param storageAreaDto the data transfer object containing the details of the area to be added
     * @return the newly created {@link StorageArea} entity
     * @throws EntityIllegalParameter if the input data is invalid or null
     * @throws AreaAlreadyExists      if an area with the same name already exists and is active
     */
    ApiResponse<StorageAreaDto> addArea(StorageAreaDto storageAreaDto) throws EntityIllegalParameter, AreaAlreadyExists;

    /**
     * Modifies an existing storage area.
     *
     * @param storageAreaDto the data transfer object containing the updated details of the area
     * @throws AreaNotFound           if the area to modify does not exist
     * @throws EntityIllegalParameter if the input data is invalid
     * @throws AreaAlreadyExists      if an area with the same name already exists and is active
     */
    void modifyArea(StorageAreaDto storageAreaDto) throws EntityIllegalParameter, AreaAlreadyExists, AreaNotFound;

    /**
     * Retrieves all active areas (areas without a modification date).
     *
     * @return a list of active {@link StorageAreaDto}
     * @throws AreaNotFound if no active areas are found
     */
    ApiResponse<List<StorageAreaDto>> getActiveAreas() throws AreaNotFound;

    /**
     * Retrieves all active areas that are associated with active packaging references.
     *
     * @return a list of {@link StorageArea} entities
     * @throws AreaNotFound if no matching areas are found
     */
    ApiResponse<List<StorageAreaDto>> getActiveAreasWithActivesReferences() throws AreaNotFound;

    /**
     * Retrieves all archived areas (areas that have been modified or deactivated).
     *
     * @return a list of archived {@link StorageArea} entities
     * @throws AreaNotFound if no archived areas are found
     */
    ApiResponse<List<StorageAreaDto>> getArchivedAreas() throws AreaNotFound;


}
