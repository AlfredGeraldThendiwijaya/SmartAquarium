package com.example.smartaquarium.network.predict

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object PredictRetrofitInstance {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)      // ⏳ koneksi ke server
        .readTimeout(60, TimeUnit.SECONDS)         // ⏳ baca respon
        .writeTimeout(60, TimeUnit.SECONDS)        // ⏳ upload file
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://predict-api-393789552767.asia-southeast1.run.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val api: PredictApiService by lazy {
        retrofit.create(PredictApiService::class.java)
    }
}