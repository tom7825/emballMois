package fr.inventory.emballmois.data.repository

import fr.inventory.emballmois.data.model.LoginRequest
import fr.inventory.emballmois.data.model.LoginResponse
import fr.inventory.emballmois.data.network.AuthApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: AuthApiService,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    val authTokenFlow: Flow<String?> = userPreferencesRepository.authToken

    suspend fun getCurrentToken(): String? {
        return userPreferencesRepository.authToken.firstOrNull()
    }

    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse> {
        return try {
            val response = apiService.login(loginRequest)
            userPreferencesRepository.saveAuthToken(response.token)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}