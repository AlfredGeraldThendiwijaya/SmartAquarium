package com.example.smartaquarium.network.analyzer

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object AnalyzerRetrofitInstance {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)      // ⏳ koneksi ke server
        .readTimeout(60, TimeUnit.SECONDS)         // ⏳ baca respon
        .writeTimeout(60, TimeUnit.SECONDS)        // ⏳ upload file
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://analyzer-api-393789552767.asia-southeast1.run.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val api: AnalyzerApiService by lazy {
        retrofit.create(AnalyzerApiService::class.java)
    }
}
