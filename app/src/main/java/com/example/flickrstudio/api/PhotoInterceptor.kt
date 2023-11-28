package com.example.flickrstudio.api

import com.example.flickrstudio.util.API_KEY
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class PhotoInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val newUrl: HttpUrl = originalRequest.url.newBuilder()
            .addQueryParameter("api_key", API_KEY)
            .addQueryParameter("format", "json")
            .addQueryParameter("nojsoncallback", "1")
            .addQueryParameter("extras", "url_s")
            .addQueryParameter("safesearch", "1")
            .build()

        val newRequest = originalRequest.newBuilder().url(newUrl).build()

        return chain.proceed(newRequest)
    }
}