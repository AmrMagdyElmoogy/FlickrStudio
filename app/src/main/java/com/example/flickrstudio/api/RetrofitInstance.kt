package com.example.flickrstudio.api

import com.example.flickrstudio.util.BASE_URL
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create

val okHttpClient = OkHttpClient.Builder().addInterceptor(PhotoInterceptor()).build()

val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create().asLenient())
    .client(okHttpClient)
    .build()

object RetrofitApi {
    val api by lazy {
        retrofit.create<FlickrApi>()
    }
}