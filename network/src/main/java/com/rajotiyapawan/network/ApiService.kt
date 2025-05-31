package com.rajotiyapawan.network

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun get(
        @Url url: String,
        @HeaderMap headers: Map<String, String> = emptyMap()
    ): String

    @POST
    suspend fun post(
        @Url url: String,
        @Body body: RequestBody,
        @HeaderMap headers: Map<String, String> = emptyMap()
    ): String
}