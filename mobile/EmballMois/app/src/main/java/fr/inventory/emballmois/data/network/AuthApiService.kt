package fr.inventory.emballmois.data.network

import fr.inventory.emballmois.data.model.LoginRequest
import fr.inventory.emballmois.data.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Inject

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
}