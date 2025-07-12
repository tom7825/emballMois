package fr.inventory.packaging.repository;

import fr.inventory.packaging.entity.PackagingReference;
import fr.inventory.packaging.entity.StorageArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository interface for managing {@link StorageArea} entities.
 */
public interface StorageAreaRepository extends JpaRepository<StorageArea,Long> {

    /**
     * Retrieves an active storage area by its name, where the modification date is null (indicating it is active).
     *
     * @param name the name of the storage area to search for
     * @return the active storage area with the specified name, or null if no such area exists
     */
    StorageArea findByStorageAreaNameAndModificationDateAreaIsNull(String name);

    /**
     * Retrieves all archived storage areas.
     *
     * @return a list of archived storage areas
     */
    List<StorageArea> findAllByModificationDateAreaIsNullAndStorageAreaArchivedTrue();

    /**
     * Retrieves all active (non-archived) storage areas.
     *
     * @return a list of active storage areas
     */
    List<StorageArea> findAllByModificationDateAreaIsNullAndStorageAreaArchivedFalse();

    /**
     * Retrieves all storage areas that have active references and are not archived.
     *
     * @return a list of active storage areas with active references
     */
    @Query("SELECT DISTINCT p.areas FROM PackagingReference p " +
            "JOIN p.areas sa " +
            "WHERE sa.modificationDateArea IS NULL " +
            "AND sa.storageAreaArchived = false " +
            "AND p.archivedPackaging = false")
    List<StorageArea> findAllByModificationDateAreaIsNullAndArchivedStorageAreaFalseAndActiveReferences();

    /**
     * Retrieves all packaging references associated with a storage area identified by its ID.
     *
     * @param oldIdArea the ID of the storage area
     * @return a list of packaging references associated with the specified storage area
     */
    @Query("Select pr FROM PackagingReference pr " +
            "JOIN pr.areas a " +
            "WHERE a.storageAreaId = :oldIdArea " +
            "AND pr.modificationDateReference is NULL")
    List<PackagingReference> getRefencesOfAreaByAreaId(Long oldIdArea);
}
