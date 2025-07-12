package fr.inventory.emballmois.data.network

import fr.inventory.emballmois.data.model.ApiResponse
import fr.inventory.emballmois.data.model.StorageArea
import retrofit2.http.GET

interface StorageAreaApiService {

    @GET("area/areas/active")
    suspend fun getStorageAreas(): ApiResponse<List<StorageArea>>

}