package fr.inventory.emballmois.data.model

data class LoginRequest(
    val password: String
)

data class LoginResponse(
    val token: String
)