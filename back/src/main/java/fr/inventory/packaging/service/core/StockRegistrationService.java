package fr.inventory.packaging.service.core;

import fr.inventory.packaging.entity.dto.StockRegistrationDto;
import fr.inventory.packaging.entity.dto.StockRegistrationForReportDto;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.registration.StockRegistrationsNotFound;

import java.util.List;

/**
 * Core service interface for managing stock registration operations.
 * Provides methods to retrieve and manipulate stock registration data associated with inventories.
 */
public interface StockRegistrationService {

    /**
     * Retrieves all stock registrations in DTO format for a given inventory ID.
     *
     * @param idInventory the ID of the inventory
     * @return a list of {@link StockRegistrationDto} representing the registrations
     * @throws EntityIllegalParameter if the inventory ID is null or invalid
     * @throws StockRegistrationsNotFound if no registrations are found for the given inventory
     */
    List<StockRegistrationDto> getRegistrationsDtoFromInventory(Long idInventory) throws EntityIllegalParameter, StockRegistrationsNotFound;

    /**
     * Retrieves stock registrations formatted for reporting, based on the given inventory ID.
     *
     * @param idInventory the ID of the inventory
     * @return a list of {@link StockRegistrationForReportDto} suitable for report generation
     * @throws EntityIllegalParameter if the inventory ID is null or invalid
     * @throws StockRegistrationsNotFound if no registrations are found for the given inventory
     */
    List<StockRegistrationForReportDto> getRegistrationsDtoForReportFromInventory(Long idInventory) throws EntityIllegalParameter, StockRegistrationsNotFound;

    /**
     * Updates the name of the packaging reference across all associated stock registrations.
     *
     * @param referenceName the new reference name to apply
     */
    void updateReferenceInStockRegistration(String referenceName);
}
