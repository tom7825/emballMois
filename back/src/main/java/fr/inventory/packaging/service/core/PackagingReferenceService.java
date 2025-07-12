package fr.inventory.packaging.service.core;

import fr.inventory.packaging.entity.PackagingReference;
import fr.inventory.packaging.entity.dto.AreaReferenceDto;
import fr.inventory.packaging.entity.dto.validation.LineValidationDto;
import fr.inventory.packaging.exceptions.entity.reference.ReferenceNotFound;

import java.util.List;

/**
 * Core service interface for managing packaging references.
 * Provides methods to retrieve and validate packaging reference data used in inventory and stock operations.
 */
public interface PackagingReferenceService {

    /**
     * Finds an active (non-archived) packaging reference by its name.
     *
     * @param referenceName the name of the reference
     * @return the found PackagingReference
     * @throws ReferenceNotFound if no active reference is found by name
     */
    PackagingReference findActiveReferenceByName(String referenceName) throws ReferenceNotFound;


    /**
     * Retrieves the names of all active references grouped by storage area.
     *
     * @return a list of {@link AreaReferenceDto} representing references per area
     */
    List<AreaReferenceDto> findAllActiveReferencesNameByAreaName();

    /**
     * Retrieves the names of all active references for a storage Area
     *
     * @return a list of String representing references in area
     */
    List<String> findAllActiveReferencesNameForAreaNameWithNoRegistrationInActiveInventory(String areaName);


    /**
     * Retrieves the minimal validation data for a given reference ID.
     *
     * @param referenceId the ID of the reference
     * @return the {@link LineValidationDto} used for display and validation
     */
    LineValidationDto findReferenceValidationById(Long referenceId);

    /**
     * Finds a packaging reference by its ID, regardless of archive status.
     *
     * @param idReference the ID of the reference
     * @return the corresponding {@link PackagingReference}
     */
    PackagingReference findReferenceById(Long idReference);

    /**
     * Update all references with production database information
     */
    void updateReferencesInformationFromExternalDB();
}
