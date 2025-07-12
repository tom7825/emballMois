package fr.inventory.emballmois.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class StorageAreaWithPackagingReferences(
    @Embedded
    val storageArea: StorageArea,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = PackagingReferenceStorageAreaCrossRef::class,
            parentColumn = "storageAreaId",
            entityColumn = "packagingReferenceId"
        )
    )
    val packagingReferences: List<PackagingReference>
)