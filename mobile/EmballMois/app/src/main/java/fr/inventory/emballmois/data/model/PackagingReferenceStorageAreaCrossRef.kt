package fr.inventory.emballmois.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "packaging_reference_storage_area_cross_ref",
    primaryKeys = ["packagingReferenceId", "storageAreaId"],
    foreignKeys = [
        ForeignKey(
            entity = PackagingReference::class,
            parentColumns = ["id"],
            childColumns = ["packagingReferenceId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = StorageArea::class,
            parentColumns = ["id"],
            childColumns = ["storageAreaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["packagingReferenceId"]),
        Index(value = ["storageAreaId"])
    ]
)
data class PackagingReferenceStorageAreaCrossRef(
    val packagingReferenceId: Long,
    val storageAreaId: Long
)