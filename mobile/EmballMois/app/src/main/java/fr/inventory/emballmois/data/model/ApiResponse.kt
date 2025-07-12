package fr.inventory.emballmois.data.model

data class ApiResponse<T>(
    val message: String?,
    val data: T?,
    val errors: Map<String, String>
)
