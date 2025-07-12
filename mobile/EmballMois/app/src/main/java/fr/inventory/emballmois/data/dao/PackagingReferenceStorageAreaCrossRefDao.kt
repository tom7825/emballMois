package fr.inventory.emballmois.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.inventory.emballmois.data.model.PackagingReference
import fr.inventory.emballmois.data.model.PackagingReferenceStorageAreaCrossRef

@Dao
interface PackagingReferenceStorageAreaCrossRefDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPackagingReferenceStorageAreaCrossRef(crossRef: PackagingReferenceStorageAreaCrossRef) : Long

    @Query("SELECT storage_area.name FROM packaging_reference_storage_area_cross_ref LEFT JOIN storage_area ON packaging_reference_storage_area_cross_ref.storageAreaId = storage_area.id WHERE packagingReferenceId = :idReference")
    suspend fun getAreasForReference(idReference: Long): List<String>

    @Query("SELECT * FROM packaging_reference_storage_area_cross_ref WHERE packagingReferenceId = :idReference AND storageAreaId = :idArea")
    suspend fun getPackagingReferenceStorageAreaCrossRef(idReference: Long, idArea : Long) : PackagingReferenceStorageAreaCrossRef?

    @Query("DELETE FROM packaging_reference_storage_area_cross_ref")
    suspend fun clearAll()


}