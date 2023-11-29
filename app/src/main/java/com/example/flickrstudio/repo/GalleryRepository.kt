package com.example.flickrstudio.repo

import android.util.Log
import com.example.flickrstudio.api.FlickrApi
import com.example.flickrstudio.api.GalleryItem
import retrofit2.HttpException

const val TAG = "Exception_http"

class GalleryRepository(
    private val api: FlickrApi
) {
    suspend fun searchPhotos(query: String): List<GalleryItem> {
        return try {
            api.searchPhotos(query).photos.galleryItems
        } catch (exception: HttpException) {
            Log.d(TAG, exception.toString())
            listOf<GalleryItem>()
        }
    }
}
