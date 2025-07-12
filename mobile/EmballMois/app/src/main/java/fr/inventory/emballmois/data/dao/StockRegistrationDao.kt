package fr.inventory.emballmois.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.inventory.emballmois.data.model.StockRegistration
import fr.inventory.emballmois.data.model.StockRegistrationDto

@Dao
interface StockRegistrationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stockRegistration: StockRegistration)

    @Query(
        "SELECT " +
                "sr.id, " +
                "sr.quantity, " +
                "sr.packagingCountState as packagingCount, " +
                "sr.commentText as comment, " +
                "pr.name as referenceName, " +
                "sa.name as storageAreaName " +
                "FROM stock_registration sr " +
                "JOIN packaging_reference pr ON sr.packagingReferenceId = pr.id " +
                "JOIN storage_area sa ON sr.storageAreaId = sa.id"
    )
    suspend fun getAllStockRegistration(): List<StockRegistrationDto>

    @Query("UPDATE stock_registration " +
            "SET quantity = :quantity, " +
            "packagingCountState = :packagingCountState, " +
            "commentText = :commentText " +
            "WHERE id = :id")
    suspend fun updateStockRegistration(
        id: Int,
        quantity: Double?,
        packagingCountState: Boolean,
        commentText: String?
    ) : Int

    @Query("DELETE FROM stock_registration WHERE id = :id")
    suspend fun deleteStockRegistration(id: Int) : Int
}