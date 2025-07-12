package fr.inventory.packaging.service.validation;

import fr.inventory.packaging.entity.PackagingReference;
import fr.inventory.packaging.entity.StockRegistration;
import fr.inventory.packaging.entity.dto.validation.RegistrationValidationDto;
import fr.inventory.packaging.exceptions.registration.IncompleteStockRegistrationException;

import java.util.Map;

/**
 * Interface for validating {@link StockRegistration} entities during creation, update,
 * or inventory validation processes.
 * Ensures consistency, completeness, and alignment with associated packaging references.
 */
public interface RegistrationValidator {

    /**
     * Validates a stock registration before it is added or modified.
     * Checks if the registration contains all required fields and is logically consistent
     * with the associated packaging reference.
     *
     * @param registration       the registration entity to validate
     * @param packagingReference the reference linked to the registration
     * @throws IncompleteStockRegistrationException if the registration is missing required information
     */
    void validateRegistrationForAddOrModify(StockRegistration registration, PackagingReference packagingReference) throws IncompleteStockRegistrationException;

    /**
     * Validates a stock registration line in the context of inventory validation.
     * Returns a map of errors by field, allowing granular feedback for UI or logging purposes.
     *
     * @param registration the DTO containing data to be validated
     * @return a map where keys are field names and values are error messages (empty if valid)
     */
    Map<String,String> validateRegistrationForInventoryValidation(RegistrationValidationDto registration);
}
