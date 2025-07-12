package fr.inventory.emballmois.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.OnConflictStrategy.Companion.ABORT
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import fr.inventory.emballmois.data.model.PackagingReference
import fr.inventory.emballmois.data.model.PackagingReferenceWithStorageAreas
import kotlinx.coroutines.flow.Flow

@Dao
interface PackagingReferenceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(packagingReferences: List<PackagingReference>)

    @Query("SELECT * FROM packaging_reference ORDER BY name ASC")
    fun getAllPackagingReferences(): Flow<List<PackagingReference>>

    @Query("DELETE FROM packaging_reference")
    suspend fun clearAll()

    @Transaction
    @Query("SELECT * FROM packaging_reference WHERE id = :packagingReferenceId")
    fun getPackagingReferenceWithStorageAreas(packagingReferenceId: Long): Flow<PackagingReferenceWithStorageAreas?>

    @Transaction
    @Query("SELECT * FROM packaging_reference")
    fun getAllPackagingReferencesWithStorageAreas(): Flow<List<PackagingReferenceWithStorageAreas>>

    @Query("SELECT * FROM packaging_reference WHERE name = :referenceName")
    suspend fun getReferenceByName(referenceName: String) : PackagingReference?

    @Insert(onConflict = ABORT)
    suspend fun insert(reference: PackagingReference) : Long

    @Update(onConflict = ABORT)
    suspend fun update(reference: PackagingReference)

    @Query("SELECT * FROM packaging_reference WHERE isNew = 1")
    suspend fun getAllNewReference() : List<PackagingReference>

    @Query("SELECT * FROM packaging_reference")
    suspend fun getAll(): List<PackagingReference>

}