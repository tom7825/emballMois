package fr.inventory.packaging.service.validation;

import fr.inventory.packaging.entity.dto.response.ApiResponse;
import fr.inventory.packaging.entity.dto.validation.LineValidationDto;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.registration.StockRegistrationsNotFound;

import java.util.List;
import java.util.Map;

/**
 * Interface for performing validation logic on inventory stock registrations..
 */
public interface InventoryValidator {

    /**
     * Performs a full validation of stock registrations for the specified inventory.
     * Each registration is validated individually and grouped by storage area and reference.
     *
     * @param inventoryId the ID of the inventory to validate
     * @return a structured map containing validation results grouped by area and reference
     * @throws EntityIllegalParameter     if an invalid reference or area is encountered
     */
    ApiResponse<Map<String, List<LineValidationDto>>> validateInventory(Long inventoryId) throws StockRegistrationsNotFound, EntityIllegalParameter;
}
