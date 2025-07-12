package fr.inventory.emballmois.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "packaging_reference")
data class PackagingReference(

    @PrimaryKey(autoGenerate = true)
    @SerializedName("idReference")
    var id: Long = 0,

    @SerializedName("referenceName")
    val name: String,

    @SerializedName("areasName")
    @Ignore
    var storageAreas: List<String>,

    @Expose(serialize = false, deserialize = false)
    var isNew: Boolean = false,

    @SerializedName("unitByPackaging")
    var unitByPackaging: Int = 0,
){
    constructor(name: String) : this(0, name, emptyList())
    @Ignore
    constructor(name: String, areaName: String) : this(0, name, listOf(areaName))
    @Ignore
    constructor(name: String, isNew: Boolean) : this(0, name, emptyList(), isNew)

}