package com.example.smartaquarium.network.analyzer

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AnalyzerApiService {
    @Multipart
    @POST("analyze")
    suspend fun analyzeImage(
        @Part image: MultipartBody.Part
    ): Response<ResponseBody>
}
