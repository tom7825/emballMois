package fr.inventory.emballmois.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "stock_registration",
    foreignKeys = [
        ForeignKey(entity = PackagingReference::class, parentColumns = ["id"], childColumns = ["packagingReferenceId"]),
        ForeignKey(entity = StorageArea::class, parentColumns = ["id"], childColumns = ["storageAreaId"])
    ],
    indices = [Index(value = ["packagingReferenceId"]),
        Index(value = ["storageAreaId"])]
)
data class StockRegistration(

    @PrimaryKey(autoGenerate = true)
    val id: Int = System.identityHashCode(Any()),

    @SerializedName("quantity")
    var quantity: Double? = null,

    @SerializedName("packagingCount")
    var packagingCountState: Boolean = false,

    @SerializedName("comment")
    var commentText: String = "",

    @SerializedName("idReference")
    var packagingReferenceId: Long? = null,

    @SerializedName("storageAreaId")
    var storageAreaId: Long? = null
)