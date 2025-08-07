package com.pokemonApp.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface GenericAPIService {
    @GET
    suspend fun get(@Url url: String): Response<ResponseBody>

    @POST
    suspend fun post(@Url url: String, @Body body: Any): Response<ResponseBody>
}