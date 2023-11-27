/*
package com.example.flickrstudio.paging

import android.net.http.HttpException
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.flickrstudio.api.FlickrApi
import com.example.flickrstudio.api.FlickrResponse
import com.example.flickrstudio.api.GalleryItem
import java.io.IOException

class GalleryPagingSource(
    private val api: FlickrApi
) : PagingSource<Int, GalleryItem>() {
    override fun getRefreshKey(state: PagingState<Int, GalleryItem>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val photo = state.closestItemToPosition(anchorPosition) ?: return null
        return 1
    }

    private fun ensureValidKey(key: Int): Int {
        return maxOf(1,key)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryItem> {
        val index = params.key ?: 1
        val range = index.until(index + params.loadSize)
        return try {
            val response = api.fetchPhotos(
                page = index
            )
            val galleryItems = response.photos.galleryItems
            LoadResult.Page(
                data = galleryItems,
                prevKey = index,
                nextKey = range.last + 1,
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }
}
*/
