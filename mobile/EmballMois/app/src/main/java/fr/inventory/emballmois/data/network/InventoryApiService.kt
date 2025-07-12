package fr.inventory.emballmois.data.network

import fr.inventory.emballmois.data.model.ApiResponse
import fr.inventory.emballmois.data.model.Inventory
import retrofit2.http.GET


interface InventoryApiService {

    @GET("inventory/exists")
    suspend fun isBackInventoryRunning(): ApiResponse<Inventory>

    @GET("inventory/create")
    suspend fun createInventory() : ApiResponse<String>
}