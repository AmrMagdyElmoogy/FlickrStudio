/*
package com.example.flickrstudio.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.flickrstudio.api.FlickrApi
import com.example.flickrstudio.paging.GalleryPagingSource

class GalleryRepository(
    private val api: FlickrApi
) {
    fun fetchPhotos() = Pager(
        config = PagingConfig(pageSize = 6, enablePlaceholders = true),
        pagingSourceFactory = {
            GalleryPagingSource(api = api)
        }
    )
}*/
