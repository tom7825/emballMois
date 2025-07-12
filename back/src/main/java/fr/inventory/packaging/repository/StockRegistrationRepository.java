package fr.inventory.packaging.repository;

import fr.inventory.packaging.entity.StockRegistration;
import fr.inventory.packaging.entity.dto.StockRegistrationDto;
import fr.inventory.packaging.entity.dto.StockRegistrationForReportDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository interface for managing {@link StockRegistration} entities.
 **/
public interface StockRegistrationRepository extends JpaRepository<StockRegistration, Long> {

    /**
     * Retrieves all stock registrations Dto associated with a specific inventory ID.
     *
     * @param idInventory the ID of the inventory
     * @return a list of stock registrations for the specified inventory
     */
    @Query("""
            SELECT new fr.inventory.packaging.entity.dto.StockRegistrationDto(
                sr.idStockRegistration,
                sr.quantity,
                sr.comment,
                r.referenceName,
                r.idReference,
                sa.storageAreaName,
                sa.storageAreaId,
                c.categoryName,
                sr.packagingCount,
                r.unitByPackaging
            )
            FROM StockRegistration sr
            JOIN sr.reference r
            JOIN sr.storageArea sa
            LEFT JOIN r.category c
            WHERE sr.inventory.idInventory = :idInventory
            """)
    List<StockRegistrationDto> findDtoByInventory_IdInventory(Long idInventory);

    /**
     * Retrieves the last existing stock registrations for a specific reference and storage area in a given inventory.
     *
     * @param areaName      the name of the storage area
     * @param referenceName the name of the reference
     * @param idInventory   the ID of the inventory
     * @return a list of the last stock registrations for the specified reference, area, and inventory
     */
    @Query("SELECT sr FROM StockRegistration sr " +
            "JOIN FETCH sr.reference r " +
            "JOIN FETCH sr.storageArea a " +
            "WHERE sr.inventory.idInventory = :idInventory " +
            "AND r.referenceName = :referenceName " +
            "AND a.storageAreaName = :areaName")
    List<StockRegistration> getLastExistingRegistrations(String areaName, String referenceName, Long idInventory);

    /**
     * Retrieves the IDs of inventories with the last existing stock registration for a given reference and area,
     * ordered by registration date.
     *
     * @param areaName      the name of the storage area
     * @param referenceName the name of the reference
     * @param pageable      pagination details
     * @return a paginated list of inventory IDs associated with the last existing stock registration
     */
    @Query("SELECT sr.inventory.idInventory FROM StockRegistration sr " +
            "JOIN sr.reference r " +
            "JOIN sr.storageArea a " +
            "WHERE r.referenceName = :referenceName " +
            "AND a.storageAreaName = :areaName " +
            "ORDER BY sr.registrationDate DESC")
    Page<Long> getInventoryIdOfLastExistingRegistration(String areaName, String referenceName, Pageable pageable);

    @Query("""
            SELECT new fr.inventory.packaging.entity.dto.StockRegistrationDto(
                sr.idStockRegistration,
                r.referenceName,
                sa.storageAreaName
            )
            FROM StockRegistration sr
            JOIN sr.reference r
            JOIN sr.storageArea sa
            JOIN r.category c
            WHERE sr.inventory.idInventory = :idInventory
            """)
    List<StockRegistrationDto> findDtoWithAreaNameAndReferenceNameByInventory_IdInventory(Long idInventory);

    /**
     * Retrieves all stock registrations and return RegistrationValidationForReportDto associated with a specific inventory ID.
     *
     * @param idInventory the ID of the inventory
     * @return a list of RegistrationValidationDto for the specified inventory
     */
    @Query("""
            SELECT new fr.inventory.packaging.entity.dto.StockRegistrationForReportDto(
                sr.idStockRegistration,
                sr.quantity,
                sr.comment,
                r.referenceName,
                r.idReference,
                sa.storageAreaName,
                sa.storageAreaId,
                sr.packagingCount,
                c.categoryName,
                c.categoryId,
                r.supplierName,
                r.unitCount,
                r.unitPrice,
                r.calculationRule,
                r.unitByPackaging,
                r.unitPriceRefFac
            )
            FROM StockRegistration sr
            JOIN sr.reference r
            JOIN sr.storageArea sa
            JOIN r.category c
            WHERE sr.inventory.idInventory = :idInventory
            """)
    List<StockRegistrationForReportDto> findDtoForReportByInventory_IdInventory(Long idInventory);

    @Query("SELECT sr FROM StockRegistration sr " +
            "JOIN FETCH sr.reference r " +
            "JOIN FETCH sr.storageArea a " +
            "WHERE sr.inventory.idInventory = :idInventory " +
            "AND r.referenceName = :referenceName ")
    List<StockRegistration> getRegistrationsForInventoryInProgressAndReference(String referenceName, Long idInventory);
}
