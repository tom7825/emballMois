package fr.inventory.emballmois.data.repository

import android.util.Log
import fr.inventory.emballmois.data.dao.PackagingReferenceDao
import fr.inventory.emballmois.data.model.PackagingReference
import fr.inventory.emballmois.data.network.PackagingReferenceApiService
import retrofit2.HttpException
import javax.inject.Inject

class PackagingReferenceRepository @Inject constructor(
    private val packagingReferenceDao: PackagingReferenceDao,
    private val packagingReferenceApiService: PackagingReferenceApiService,
    private val packagingReferenceStorageAreaCrossRefRepository: PackagingReferenceStorageAreaCrossRefRepository
) {
    suspend fun loadReferenceForArea(areaName: String) {
        try {
            val networkPackagingReferences =
                packagingReferenceApiService.getReferenceForStorageArea(areaName).data
            if (networkPackagingReferences?.isNotEmpty() == true) {
                packagingReferenceDao.insertAll(networkPackagingReferences)
                packagingReferenceStorageAreaCrossRefRepository.insertPackagingReferenceStorageAreaCrossRef(
                    networkPackagingReferences,
                    areaName
                )
            }
        }catch (e : HttpException){
            if (e.code() == 404) {
                Log.d("PackagingReferenceRepository", "Aucune référence trouvée pour la zone : $areaName")
            }else{
                throw e
            }
        }
    }

    suspend fun clearAll() {
        packagingReferenceDao.clearAll()
        packagingReferenceStorageAreaCrossRefRepository.clearAll()
    }

    suspend fun getReferenceByName(referenceName: String) : PackagingReference? {
        return packagingReferenceDao.getReferenceByName(referenceName)
    }

    suspend fun saveNewReferences() {
        try{
            packagingReferenceDao.getAllNewReference().forEach { newReference ->
                newReference.storageAreas = packagingReferenceStorageAreaCrossRefRepository.getAreasForReference(newReference.id)
                packagingReferenceApiService.saveNewReference(newReference)
                newReference.isNew = false
                packagingReferenceDao.update(newReference)
                Log.d("PackagingReferenceRepository", "Nouvelle référence sauvegardée : ${newReference.name}")
            }
        }catch (e : Exception){
            Log.e("PackagingReferenceRepository", "Erreur lors de la sauvegarde des nouvelles références", e)
            throw e
        }
    }

    suspend fun getAllPackagingReferences() : List<PackagingReference> {
        return packagingReferenceDao.getAll()

    }
}