package fr.inventory.emballmois.data.network

import fr.inventory.emballmois.data.model.ApiResponse
import fr.inventory.emballmois.data.model.StockRegistrationDto
import retrofit2.http.Body
import retrofit2.http.POST

interface StockRegistrationApiService {

    @POST("registration/add/without/verification")
    suspend fun saveStockRegistrations(@Body stockRegistration: StockRegistrationDto)
}