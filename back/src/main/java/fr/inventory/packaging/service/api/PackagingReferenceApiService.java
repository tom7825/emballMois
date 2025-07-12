package fr.inventory.packaging.service.api;

import fr.inventory.packaging.entity.dto.PackagingReferenceDto;
import fr.inventory.packaging.entity.dto.response.ApiResponse;
import fr.inventory.packaging.exceptions.entity.EntityAlreadyExist;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.entity.reference.ReferenceNotFound;
import fr.inventory.packaging.exceptions.entity.reference.IncompleteReference;
import fr.inventory.packaging.exceptions.entity.storage.area.AreaNotFound;

import java.util.List;

/**
 * Service implementation for managing packaging references.
 * Handles operations such as creation, modification, retrieval, and validation
 * of packaging references, including logic for handling archived and active states.
 */
public interface PackagingReferenceApiService {

    /**
     * Adds a new packaging reference if it does not already exist.
     *
     * @param packagingReferenceDto the data transfer object containing reference data
     * @throws EntityIllegalParameter if the input data is null or invalid
     * @throws EntityAlreadyExist if a reference with the same name already exists
     */
    void addReference(PackagingReferenceDto packagingReferenceDto) throws EntityIllegalParameter, EntityAlreadyExist, IncompleteReference;

    /**
     * Retrieves all active packaging references (not archived).
     *
     * @return a list of active packaging reference DTOs
     * @throws ReferenceNotFound if no active references are found
     */
    ApiResponse<List<PackagingReferenceDto>> findAllActiveReference() throws ReferenceNotFound;

    /**
     * Retrieves packaging references assigned to a given area name.
     *
     * @param areaName the name of the storage area
     * @return a list of packaging reference DTOs
     * @throws ReferenceNotFound if no references are found in the given area
     * @throws AreaNotFound if the area name is unknown
     * @throws EntityIllegalParameter if the area name is null or empty
     */
    ApiResponse<List<PackagingReferenceDto>> getActiveReferencesByAreaName(String areaName) throws ReferenceNotFound, AreaNotFound, EntityIllegalParameter;

    /**
     * Modifies an existing active packaging reference.
     * A new version of the reference is created and the old one is marked as modified.
     *
     * @param packagingReferenceDto the new data to apply
     * @throws EntityIllegalParameter if the data is invalid
     * @throws ReferenceNotFound if the reference does not exist
     */
    void modifyReference(PackagingReferenceDto packagingReferenceDto) throws EntityIllegalParameter, ReferenceNotFound, IncompleteReference;

    /**
     * Retrieves all archived references (inactive and unmodified).
     *
     * @return list of archived reference DTOs
     * @throws ReferenceNotFound if no archived references are found
     */
    ApiResponse<List<PackagingReferenceDto>> findAllArchivedReference() throws ReferenceNotFound;

    /**
     * Retrieves references that are not assigned to the specified area.
     *
     * @param areaName the name of the excluded area
     * @return list of reference DTOs not in the area
     * @throws EntityIllegalParameter if areaName is null or blank
     * @throws ReferenceNotFound if no references are found
     */
    ApiResponse<List<PackagingReferenceDto>> findAllReferencesExcludingArea(String areaName) throws EntityIllegalParameter, ReferenceNotFound;

    /**
     * Adds a new packaging reference (with minimal information) if it does not already exist.
     *
     * @param packagingReferenceDto the data transfer object containing reference data
     * @throws EntityIllegalParameter if the input data is null or invalid
     */
    void addMinimalReference(PackagingReferenceDto packagingReferenceDto) throws EntityIllegalParameter;

    /**
     * Delete an Area in a Packaging Reference
     * @param referenceId id of the Reference
     * @param areaName name of the Area
     */
    void deleteArea(Long referenceId, String areaName) throws EntityIllegalParameter;
}
