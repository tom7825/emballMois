package fr.inventory.emballmois.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import fr.inventory.emballmois.data.model.StorageArea
import fr.inventory.emballmois.data.model.StorageAreaWithPackagingReferences
import kotlinx.coroutines.flow.Flow

@Dao
interface StorageAreaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(storageAreas: List<StorageArea>)

    @Query("SELECT * FROM storage_area ORDER BY name ASC")
    fun getAllStorageAreas(): Flow<List<StorageArea>>

    @Query("DELETE FROM storage_area")
    suspend fun clearAll()

    @Query("SELECT id FROM storage_area WHERE name = :name LIMIT 1")
    suspend fun getStorageAreaIdByName(name: String): Long?

    @Transaction
    @Query("SELECT * FROM storage_area")
    fun getAllStorageAreasWithPackagingReferences(): Flow<List<StorageAreaWithPackagingReferences>>

    @Transaction
    @Query("SELECT * FROM storage_area WHERE id = :storageAreaId")
    fun getStorageAreaWithPackagingReferences(storageAreaId: Long): Flow<StorageAreaWithPackagingReferences?>
}