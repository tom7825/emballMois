package fr.inventory.emballmois.data.model

data class StockRegistrationDto(

    var id: Int = System.identityHashCode(Any()),

    var quantity: Double? = 0.0,

    var packagingCount: Boolean = false,

    var comment: String = "",

    var referenceName: String = "",

    var storageAreaName: String? = ""
)