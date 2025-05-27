package com.example.smartaquarium.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
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
data class deleteUnit(
    val userId: String,
    val unitId: String,
)

data class deleteResponse(
    val message: String,
    val schedules: List<deleteUnit>? = null  // ✅ Ubah ke List kalau API balikin array
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
data class ph_reading(
    val latestData: LatestData? = null
)
data class tds_reading(
    val latestData: LatestData? = null
)

data class ScheduleRequest(
    val idUnit: String, // ✅ Sesuai dengan API (unitId → idUnit)
    val time: String
)

// Data class untuk response dari API setelah menambahkan jadwal
data class ScheduleResponse(
    val message: String,
    val schedules: List<ScheduleData>? = null  // ✅ Ubah ke List kalau API balikin array
)



// Data class untuk menyimpan data jadwal yang dikembalikan API
data class ScheduleData(
    val id: String,  // ✅ ID dari Firebase (hash ID)
    val time: String // ✅ Format waktu tetap String
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

    @GET("get-latest-sensor/{unitId}/ph")
    fun getLatestph(
        @Path("unitId") unitId: String,
        @Header("Authorization") auth: String
    ): Call<ph_reading>

    @GET("get-latest-sensor/{unitId}/tds")
    fun getLatestTds(
        @Path("unitId") unitId: String,
        @Header("Authorization") auth: String
    ): Call<tds_reading>

    @POST("schedule-servo")
    suspend fun addSchedule(@Body request: ScheduleRequest): ScheduleResponse

    @GET("get-servo-schedule/{unit_id}")
    suspend fun getServoSchedule(@Path("unit_id") unitId: String): ScheduleResponse

    @DELETE("delete-servo-schedule/{unit_id}/{schedule_id}")
    suspend fun deleteSchedule(
        @Path("unit_id") unitId: String,
        @Path("schedule_id") scheduleId: String
    ): ScheduleResponse

    @DELETE("delete-unit/{userId}/{unitId}")
    suspend fun deleteUnit(
        @Path("userId") userId: String,
        @Path("unitId") unitId: String
    ):deleteResponse

}




