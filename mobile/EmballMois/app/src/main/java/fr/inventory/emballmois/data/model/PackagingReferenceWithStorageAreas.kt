package fr.inventory.emballmois.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PackagingReferenceWithStorageAreas(
    @Embedded
    val packagingReference: PackagingReference,

    @Relation(
        parentColumn = "id",
        entity = StorageArea::class,
        entityColumn = "id",
        associateBy = Junction(
            value = PackagingReferenceStorageAreaCrossRef::class,
            parentColumn = "packagingReferenceId",
            entityColumn = "storageAreaId"
        )
    )
    val storageAreas: List<StorageArea>
)