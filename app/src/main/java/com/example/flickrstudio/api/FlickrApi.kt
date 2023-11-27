package com.example.flickrstudio.api

import com.example.flickrstudio.util.API_KEY
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApi {
    @GET(
        "/services/rest/?method=flickr.interestingness.getList" +
                "&api_key=$API_KEY" +
                "&format=json" +
                "&nojsoncallback=1" +
                "&extras=url_s"
    )
    suspend fun fetchPhotos(
    ): FlickrResponse
}