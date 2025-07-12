package fr.inventory.packaging.service.api;

import fr.inventory.packaging.entity.dto.StockRegistrationDto;
import fr.inventory.packaging.entity.dto.response.ApiResponse;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.inventory.NoInventoryInProgress;
import fr.inventory.packaging.exceptions.registration.StockRegistrationsNotFound;
import fr.inventory.packaging.exceptions.registration.IncompleteStockRegistrationException;

import java.util.List;

/**
 * Implementation for managing stock registration operations,
 * such as creation, validation, updating, and retrieval.
 */
public interface StockRegistrationApiService {

    /**
     * Adds a new stock registration entry.
     *
     * @param stockRegistrationDto DTO containing stock registration data
     * @return the created stock registration as DTO
     * @throws NoInventoryInProgress if no active inventory exists
     * @throws EntityIllegalParameter if input is null or invalid
     * @throws IncompleteStockRegistrationException if the registration is incomplete
     */
    ApiResponse<StockRegistrationDto> addStockRegistration(StockRegistrationDto stockRegistrationDto) throws NoInventoryInProgress, EntityIllegalParameter, IncompleteStockRegistrationException;

    /**
     * Retrieves stock registrations in DTO form for a given inventory.
     *
     * @param idInventory inventory ID
     * @return list of stock registration DTOs
     * @throws StockRegistrationsNotFound if none are found
     * @throws EntityIllegalParameter if ID is null
     */
    ApiResponse<List<StockRegistrationDto>> getRegistrationsDtoFromInventory(Long idInventory) throws StockRegistrationsNotFound, EntityIllegalParameter;

    /**
     * Retrieves minimal DTOs used for validation view from a given inventory.
     *
     * @param idInventory inventory ID
     * @return list of DTOs containing area and reference names
     * @throws EntityIllegalParameter if ID is null
     * @throws StockRegistrationsNotFound if none found
     */
     ApiResponse<List<StockRegistrationDto>> getRegistrationsValidationFromInventory(Long idInventory) throws EntityIllegalParameter, StockRegistrationsNotFound;

    /**
     * Copies last existing registrations from a previous inventory to the current one.
     *
     * @param areaName name of the storage area
     * @param referenceName name of the packaging reference
     * @throws StockRegistrationsNotFound if no previous data found
     */
    void addLastExistingRegistrationsToCurrentInventory(String areaName, String referenceName) throws StockRegistrationsNotFound;

    /**
     * Updates a stock registration entry.
     *
     * @param stockRegistrationDto the updated stock registration DTO
     * @throws EntityIllegalParameter if the DTO or ID is null
     * @throws StockRegistrationsNotFound if the registration does not exist
     * @throws IncompleteStockRegistrationException if validation fails
     */
    void updateStockRegistration(StockRegistrationDto stockRegistrationDto) throws EntityIllegalParameter, StockRegistrationsNotFound, IncompleteStockRegistrationException;

    /**
     * Retrieves a single stock registration by its unique ID.
     *
     * @param idRegistration the registration ID
     * @return an {@link ApiResponse} wrapping the stock registration DTO
     * @throws EntityIllegalParameter if the ID is null
     * @throws StockRegistrationsNotFound if the registration is not found
     */
    ApiResponse<StockRegistrationDto> getRegistrationsDtoById(Long idRegistration) throws EntityIllegalParameter, StockRegistrationsNotFound;

    /**
     * Adds a stock registration without applying standard validation rules.
     * <p>
     * This is intended for trusted or internal operations.
     *
     * @param stockRegistrationDto the DTO containing the stock registration details
     * @throws NoInventoryInProgress if no inventory is currently active
     * @throws EntityIllegalParameter if the DTO is null or contains invalid data
     */
    void addStockRegistrationWithoutVerification(StockRegistrationDto stockRegistrationDto) throws NoInventoryInProgress, EntityIllegalParameter;

    /**
     * Copies last existing registrations for all references in storage Area from the previous inventory to the current one.
     *
     * @param areaName name of the storage area
     */
    Integer addLastExistingRegistrationsForAllReferencesToCurrentInventory(String areaName);
}
