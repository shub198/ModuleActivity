package com.rajotiyapawan.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer

class CurlLoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val curlCommand = buildCurlCommand(request)
        Log.d("CURL ::", curlCommand)

        return chain.proceed(request)
    }

    private fun buildCurlCommand(request: Request): String {
        val curlBuilder = StringBuilder("curl -X ${request.method} \\\n")

        // Add headers
        for ((name, value) in request.headers) {
            curlBuilder.append("  -H \"$name: $value\" \\\n")
        }

        // Add body if POST/PUT
        request.body?.let { body ->
            val buffer = Buffer()
            body.writeTo(buffer)
            val contentType = body.contentType()?.toString() ?: "application/json"
            val requestBody = buffer.readUtf8()
            curlBuilder.append("  -H \"Content-Type: $contentType\" \\\n")
            curlBuilder.append("  --data-raw '${requestBody.replace("\n", "")}' \\\n")
        }

        // Add URL
        curlBuilder.append("  \"${request.url}\"")

        return curlBuilder.toString()
    }
}
