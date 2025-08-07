package com.pokemonApp.network

import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

object NetworkRepository {
    val gson = Gson()
    val service: GenericAPIService = NetworkModule.createService(GenericAPIService::class.java)

    suspend inline fun <reified T : Any> get(
        url: String
    ): ApiResponse<T> {
        return try {
            val response = service.get(url)
            if (response.isSuccessful) {
                val body = response.body()?.string()
                val parsed = gson.fromJson(body, T::class.java)
                ApiResponse.Success(parsed)
            } else {
                ApiResponse.Error("Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    suspend inline fun <reified T> post(url: String, body: Any): ApiResponse<T> {
        return try {
            val response = service.post(url, body)
            if (response.isSuccessful) {
                val responseBody = response.body()?.string()
                val parsed = gson.fromJson(responseBody, T::class.java)
                ApiResponse.Success(parsed)
            } else {
                ApiResponse.Error("Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResponse.Error("Exception: ${e.message}")
        }
    }

    fun <T> handleError(e: Exception): ApiResponse<T> {
        return when (e) {
            is HttpException -> ApiResponse.Error("HTTP ${e.code()}: ${e.message()}", e.code())
            is IOException -> ApiResponse.Error("Network Error: ${e.message}")
            else -> ApiResponse.Error("Unknown Error: ${e.message}")
        }
    }
}