package fr.inventory.emballmois.data.network

import fr.inventory.emballmois.data.model.ApiResponse
import fr.inventory.emballmois.data.model.PackagingReference
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PackagingReferenceApiService {

    @GET("reference/references/area/{areaName}")
    suspend fun getReferenceForStorageArea(@Path("areaName") areaName: String) : ApiResponse<List<PackagingReference>>

    @POST("reference/add/minimal")
    suspend fun saveNewReference(@Body reference: PackagingReference) : ApiResponse<PackagingReference>
}