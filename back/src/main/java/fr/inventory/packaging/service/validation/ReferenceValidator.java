package fr.inventory.packaging.service.validation;

import fr.inventory.packaging.entity.PackagingReference;
import fr.inventory.packaging.entity.dto.PackagingReferenceDto;
import fr.inventory.packaging.entity.dto.validation.LineValidationDto;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.entity.reference.IncompleteReference;

import java.util.Map;

/**
 * Interface for validating {@link PackagingReference} entities and related DTOs.
 * This service ensures that references conform to required business rules,
 * including completeness, packaging logic, and consistency before inventory usage or registration.
 */
public interface ReferenceValidator {

    /**
     * Validates the integrity of a {@link PackagingReference} entity.
     *
     * @param reference the reference to validate
     * @throws EntityIllegalParameter if the reference is null or structurally invalid
     * @throws IncompleteReference if required fields are missing
     */
    void validateReference(PackagingReference reference) throws EntityIllegalParameter, IncompleteReference;

    /**
     * Validates the integrity of a {@link PackagingReferenceDto}.
     *
     * @param referenceDto the DTO to validate
     * @throws EntityIllegalParameter if the DTO is null or malformed
     * @throws IncompleteReference if required data is missing
     */
    void validateReference(PackagingReferenceDto referenceDto) throws EntityIllegalParameter, IncompleteReference;

    /**
     * Validates a reference during inventory line validation.
     *
     * @param reference the reference line DTO to validate
     * @param packagingCount flag indicating whether the reference uses packaging count logic
     * @throws EntityIllegalParameter if the DTO is invalid or inconsistent with packaging expectations
     */
    void validateReferenceForInventoryValidation(LineValidationDto reference, Boolean packagingCount) throws EntityIllegalParameter;

    /**
     * Validates reference business rules during stock registration add or modify operations.
     *
     * @param reference the reference entity being registered
     * @param packagingCount whether the registration uses packaging count
     * @return a map of field names to error messages for any validation failures (empty if valid)
     */
    Map<String,String> validateReferenceForAddOrModifyStockRegistration(PackagingReference reference, Boolean packagingCount);
}
