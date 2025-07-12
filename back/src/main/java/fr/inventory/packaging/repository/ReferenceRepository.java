package fr.inventory.packaging.repository;

import fr.inventory.packaging.entity.PackagingReference;
import fr.inventory.packaging.entity.dto.AreaReferenceDto;
import fr.inventory.packaging.entity.dto.validation.LineValidationDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * Repository interface for managing {@link PackagingReference} entities.
 **/
public interface ReferenceRepository extends JpaRepository<PackagingReference, Long> {

    /**
     * Retrieves all active packaging references (where archived is false and modification date is null),
     * ordered by reference ID in descending order.
     *
     * @return a list of active packaging references ordered by reference ID in descending order
     */
    @Query("SELECT r FROM PackagingReference r LEFT JOIN FETCH r.category LEFT JOIN FETCH r.areas WHERE r.archivedPackaging = false AND r.modificationDateReference IS NULL ORDER BY r.idReference DESC")
    List<PackagingReference> findAllByArchivedPackagingFalseAndModificationDateReferenceIsNullOrderByIdReferenceDesc();

    /**
     * Retrieves a packaging reference by its name, where the modification date is null (indicating it is the most recent),
     * and the reference is not archived.
     *
     * @param referenceName the name of the reference to search for
     * @return the active packaging reference with the specified name, or null if no such reference exists
     */
    @Query("SELECT r FROM PackagingReference r LEFT JOIN FETCH r.category LEFT JOIN FETCH r.areas WHERE r.archivedPackaging = false AND r.modificationDateReference IS NULL AND r.referenceName = :referenceName")
    PackagingReference findByReferenceNameAndModificationDateReferenceIsNullAndArchivedPackagingFalse(String referenceName);

    /**
     * Retrieves a packaging reference by its name, where the modification date is null (indicating it is the most recent).
     *
     * @param referenceName the name of the reference to search for
     * @return the active packaging reference with the specified name, or null if no such reference exists
     */
    @Query("SELECT r FROM PackagingReference r LEFT JOIN FETCH r.category LEFT JOIN FETCH r.areas WHERE r.modificationDateReference IS NULL AND r.referenceName = :referenceName")
    PackagingReference findByReferenceNameAndModificationDateReferenceIsNull(String referenceName);

    /**
     * Retrieves all packaging references that are archived (where archived is true and modification date is null).
     *
     * @return a list of archived packaging references with a null modification date
     */
    @Query("SELECT r FROM PackagingReference r JOIN FETCH r.category LEFT JOIN FETCH r.areas WHERE r.archivedPackaging = true AND r.modificationDateReference IS NULL ORDER BY r.idReference DESC")
    List<PackagingReference> findAllByArchivedPackagingTrueAndModificationDateReferenceIsNullOrderByIdReferenceDesc();

    /**
     * Retrieves all packaging references associated with a specific area ID, ordered by reference ID in descending order.
     *
     * @param id the ID of the storage area
     * @return a list of packaging references associated with the specified area ID, ordered by reference ID in descending order
     */
    @Query("SELECT r FROM PackagingReference r JOIN FETCH r.areas ar WHERE ar.storageAreaId = :id AND r.modificationDateReference is NULL AND r.archivedPackaging = false")
    List<PackagingReference> findActiveReferencesByAreaIdOrderByIdReferenceDesc(Long id);

    /**
     * Retrieves all packaging references that are not associated with a specific storage area.
     *
     * @param areaName the name of the storage area
     * @return a list of packaging references that are not associated with the specified area name
     */
    @Query("""
                SELECT r FROM PackagingReference r
                WHERE r.modificationDateReference IS NULL
                AND r NOT IN (
                    SELECT ref FROM PackagingReference ref
                    JOIN ref.areas a
                    WHERE a.storageAreaName = :areaName
                )
            """)
    List<PackagingReference> getReferencesExcludingArea(String areaName);

    @Query("""
                            SELECT new fr.inventory.packaging.entity.dto.AreaReferenceDto(a.storageAreaName,
                            new fr.inventory.packaging.entity.dto.validation.LineValidationDto(
                            pr.idReference,
                            pr.referenceName,
                            pr.calculationRule,
                            pr.unitPrice,
                            pr.unitByPackaging,
                            ca.categoryName,
                            pr.unitCount
                            )
            )
                            FROM PackagingReference pr
                            JOIN pr.areas a
                            JOIN pr.category ca
                            WHERE pr.archivedPackaging = false AND a.storageAreaArchived = false
                            AND pr.modificationDateReference is NULL AND a.modificationDateArea is NULL
            """)
    List<AreaReferenceDto> findAllActiveReferenceNamesByArea();

    @Query("""
                SELECT new fr.inventory.packaging.entity.dto.validation.LineValidationDto(
                pr.idReference,
                pr.referenceName,
                pr.calculationRule,
                pr.unitPrice,
                pr.unitByPackaging,
                c.categoryName,
                pr.unitCount
                )
                FROM PackagingReference pr
                LEFT JOIN pr.category c
                WHERE pr.idReference = :referenceId
            """)
    LineValidationDto findDtoById(Long referenceId);

    @Query("SELECT r FROM PackagingReference r " +
            "JOIN FETCH r.category " +
            "LEFT JOIN FETCH r.areas " +
            "WHERE r.idReference = :idReference")
    PackagingReference findReferenceById(Long idReference);

    @Query("""
                SELECT pr.referenceName
                FROM PackagingReference pr
                JOIN pr.areas a
                WHERE pr.archivedPackaging = false
                  AND a.storageAreaArchived = false
                  AND pr.modificationDateReference IS NULL
                  AND a.modificationDateArea IS NULL
                  AND a.storageAreaName = :areaName
                  AND NOT EXISTS (
                      SELECT 1
                      FROM StockRegistration sr
                      WHERE sr.reference = pr
                        AND sr.inventory.idInventory = :idInventory
                        AND sr.storageArea.storageAreaName = :areaName
                  )
            """)
    List<String> findAllActiveReferenceNamesForAreaWithNoRegistrationInCurrentInventory(String areaName, Long idInventory);
}
