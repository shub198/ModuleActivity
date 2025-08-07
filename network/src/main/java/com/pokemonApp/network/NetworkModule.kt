package com.pokemonApp.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val requestWithHeaders = originalRequest.newBuilder()
//                .addHeader("Authorization", "Bearer $AuthToken")
                .addHeader("Content-Type", "application/json")
                .build()
            return@addInterceptor chain.proceed(requestWithHeaders)
        }
        .addInterceptor(logging)
        .addInterceptor(CurlLoggingInterceptor())
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(POKE_BaseUrl) // can be overridden in dynamic calls
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}