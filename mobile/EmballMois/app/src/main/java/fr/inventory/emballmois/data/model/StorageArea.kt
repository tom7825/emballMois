package fr.inventory.emballmois.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "storage_area")
data class StorageArea(

    @PrimaryKey
    @SerializedName("storageAreaId")
    val id: Long,

    @SerializedName("storageAreaName")
    val name: String
)