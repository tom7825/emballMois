package fr.inventory.emballmois.data.repository

import android.util.Log
import fr.inventory.emballmois.data.dao.StorageAreaDao
import fr.inventory.emballmois.data.model.StorageArea
import fr.inventory.emballmois.data.network.StorageAreaApiService
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

class StorageAreaRepository @Inject constructor(
    private val storageAreaDao: StorageAreaDao,
    private val storageAreaApiService: StorageAreaApiService
) {

    val allStorageAreas: Flow<List<StorageArea>> = storageAreaDao.getAllStorageAreas()

    suspend fun refreshStorageAreas() {
        try {
            val networkStorageAreas = storageAreaApiService.getStorageAreas().data
            if (networkStorageAreas?.isNotEmpty() == true) {
                storageAreaDao.clearAll()
                storageAreaDao.insertAll(networkStorageAreas)
                Log.d("StorageAreaRepository", "Zones insérées dans la base de données.")
            } else {
                Log.d("StorageAreaRepository", "Aucune zone récupérée du réseau, ou liste vide.")
            }

        } catch (e: Exception) {
            Log.e("StorageAreaRepository", "Erreur lors du rafraîchissement des zones: ${e.message}", e)
            if (e is UnknownHostException) {
                Log.e("StorageAreaRepository", "Impossible de joindre l'hôte. Vérifiez la connexion internet ou l'URL de l'API.")
            }
            if (e is HttpException) {
                throw e;
            }
        }
    }

    suspend fun clearAll() {
        storageAreaDao.clearAll()
    }
}