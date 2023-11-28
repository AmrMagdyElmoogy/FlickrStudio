package com.example.flickrstudio.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickrstudio.api.FlickrApi
import com.example.flickrstudio.api.GalleryItem
import com.example.flickrstudio.api.RetrofitApi
import com.example.flickrstudio.repo.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val TAG = "PhotoGalleryViewModel"

class FlickrViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PhotoGalleryUiState())
    private val repo: PreferencesRepository = PreferencesRepository.get()
    val uiState: StateFlow<PhotoGalleryUiState>
        get() = _uiState.asStateFlow()
    val api: FlickrApi = RetrofitApi.api

    init {
        viewModelScope.launch {
            try {
                repo.storedQuery.collectLatest {
                    searchPhotos(it)
                }

                Log.d(TAG, "$_uiState")
            } catch (e: Exception) {
                Log.d("ExceptionHandling", e.toString())
            }
        }
    }

    fun setQuery(query: String) {
        viewModelScope.launch {
            repo.setStoredQuery(query)
        }
    }

    private fun searchPhotos(query: String) {
        viewModelScope.launch {
            val items = api.searchPhotos(query).photos.galleryItems
            _uiState.update { oldState ->
                oldState.copy(
                    images = items,
                    query = query
                )
            }
        }
    }

    private suspend fun fetchAllPhotos(): List<GalleryItem> = api.fetchPhotos().photos.galleryItems
}

data class PhotoGalleryUiState(
    val images: List<GalleryItem> = listOf(),
    val query: String = ""
)