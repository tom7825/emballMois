package fr.inventory.emballmois.data.repository

import android.util.Log
import fr.inventory.emballmois.data.dao.PackagingReferenceDao
import fr.inventory.emballmois.data.dao.PackagingReferenceStorageAreaCrossRefDao
import fr.inventory.emballmois.data.dao.StorageAreaDao
import fr.inventory.emballmois.data.model.PackagingReference
import fr.inventory.emballmois.data.model.PackagingReferenceStorageAreaCrossRef
import fr.inventory.emballmois.data.model.StorageAreaWithPackagingReferences
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class PackagingReferenceStorageAreaCrossRefRepository @Inject constructor(
    private val packagingReferenceStorageAreaCrossRefDao: PackagingReferenceStorageAreaCrossRefDao,
    private val packagingReferenceDao: PackagingReferenceDao,
    private val storageAreaDao: StorageAreaDao
) {
    suspend fun insertPackagingReferenceStorageAreaCrossRef(
        packagingReferences: List<PackagingReference>,
        storageAreaName: String
    ) {
        if (storageAreaName.isNotEmpty()) {
            val storageAreaId = storageAreaDao.getStorageAreaIdByName(storageAreaName)
            if (!packagingReferences.isEmpty() && storageAreaId != null) {
                for (reference in packagingReferences) {
                    var crossRef = packagingReferenceStorageAreaCrossRefDao.getPackagingReferenceStorageAreaCrossRef(reference.id,storageAreaId)
                    if(crossRef == null) {
                        crossRef =
                            PackagingReferenceStorageAreaCrossRef(reference.id, storageAreaId)
                        val result = packagingReferenceStorageAreaCrossRefDao.insertPackagingReferenceStorageAreaCrossRef(
                            crossRef
                        )
                        Log.d("InsertCrossRef", "Résultat = $result")
                    }else{
                        Log.d("PackagingReferenceStorageAreaCrossRefRepository", "Référence déjà existante : ${reference.name} dans la zone : $storageAreaName")
                    }
                }
            }
        }
    }

    suspend fun getStorageAreaWithPackagingReferences(storageAreaId: Long): StorageAreaWithPackagingReferences? {
        return storageAreaDao.getStorageAreaWithPackagingReferences(storageAreaId).firstOrNull()
    }

    suspend fun addReference(reference: PackagingReference, storageAreaName: String) {
        try {

            val referenceId = packagingReferenceDao.insert(reference)
            //Si référence bien ajouté
            if (referenceId != -1L) {
                 val insertedReference = reference.copy(id = referenceId)
                insertPackagingReferenceStorageAreaCrossRef(
                    listOf<PackagingReference>(insertedReference),
                    storageAreaName
                )
            //Si référence déjà existante ajout de la zone de stockage à la référence
            } else {
                packagingReferenceDao.getReferenceByName(reference.name)?.let {
                    insertPackagingReferenceStorageAreaCrossRef(
                        listOf<PackagingReference>(it),
                        storageAreaName
                    )
                }
            }
        } catch (e: Exception) {
            Log.d(
                "PackagingReferenceStorageAreaCrossRefRepository",
                "Erreur d'ajout de référence: ${e.message}"
            )
            throw e
        }
    }

    suspend fun getAreasForReference(idReference: Long) : List<String>{
       return packagingReferenceStorageAreaCrossRefDao.getAreasForReference(idReference)
    }

    suspend fun clearAll() {
        packagingReferenceStorageAreaCrossRefDao.clearAll()
    }
}