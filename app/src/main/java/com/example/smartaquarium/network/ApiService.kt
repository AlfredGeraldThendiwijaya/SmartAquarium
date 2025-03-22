package com.example.smartaquarium.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

// Data class untuk response dari daftar akuarium
data class AquariumResponse(
    val unitId: String,
    val unitName: String
)

// Data class untuk request menambahkan unit baru
data class AddUnitRequest(
    val userId: String,
    val unitId: String,
    val unitName: String
)
data class turbidity_reading(
    val latestData: LatestData? = null
)

data class LatestData(
    val value: Double? = null
)

data class temperature_reading(
    val latestData: LatestData? = null
)




// Interface Retrofit untuk API Smart Aquarium
interface ApiService {

    // Ambil daftar akuarium berdasarkan userId
    @GET("get-units/{userId}")
    suspend fun getAquariums(@Path("userId") userId: String): List<AquariumResponse>

    // Tambah akuarium baru ke sistem
    @POST("add-unit")
    suspend fun addAquarium(@Body request: AddUnitRequest)

    @GET("get-latest-sensor/{unitId}/turbidity")
    fun getLatestTurbidity(
        @Path("unitId") unitId: String,
        @Header("Authorization") auth: String
    ): Call<turbidity_reading>

    @GET("get-latest-sensor/{unitId}/temperature")
    fun getLatestTemperature(
        @Path("unitId") unitId: String,
        @Header("Authorization") auth: String
    ): Call<temperature_reading>


}


