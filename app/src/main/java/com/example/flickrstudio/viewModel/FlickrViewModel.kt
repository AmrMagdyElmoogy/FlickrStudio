package com.example.flickrstudio.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.flickrstudio.api.FlickrApi
import com.example.flickrstudio.api.GalleryItem
import com.example.flickrstudio.api.RetrofitApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import retrofit2.Retrofit

const val TAG = "PhotoGalleryViewModel"

class FlickrViewModel : ViewModel() {


    private val _items = MutableStateFlow<List<GalleryItem>>(emptyList())

    val items: StateFlow<List<GalleryItem>>
        get() = _items.asStateFlow()
    val api: FlickrApi = RetrofitApi.api

    init {
        viewModelScope.launch {
            try {
                _items.value = api.fetchPhotos().photos.galleryItems
                Log.d(TAG, "$_items")
            } catch (e: Exception) {
                Log.d("Exception", e.toString())
            }
        }
    }
}