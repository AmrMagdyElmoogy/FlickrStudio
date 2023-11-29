package com.example.flickrstudio.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickrstudio.api.FlickrApi
import com.example.flickrstudio.api.GalleryItem
import com.example.flickrstudio.api.RetrofitApi
import com.example.flickrstudio.repo.GalleryRepository
import com.example.flickrstudio.repo.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val TAG = "PhotoGalleryViewModel"

enum class States {
    LOADING,
    EMPTY,
    SUCCESS,
}

class FlickrViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PhotoGalleryUiState())

    val uiState: StateFlow<PhotoGalleryUiState>
        get() = _uiState.asStateFlow()
    private val _listOfStoredQueries = MutableStateFlow(emptyList<String>())

    val listOfStoredQueries: StateFlow<List<String>>
        get() = _listOfStoredQueries.asStateFlow()
    private val repo: PreferencesRepository = PreferencesRepository.get()

    val api: FlickrApi = RetrofitApi.api

    private val galleryRepo = GalleryRepository(api)

    init {
        viewModelScope.launch {
            repo.storedQuery.collectLatest {
                val items = galleryRepo.searchPhotos(it)
                _uiState.update { oldState ->
                    oldState.copy(
                        images = items,
                        query = it,
                        states = if (items.isEmpty()) States.EMPTY else States.SUCCESS
                    )
                }
            }
        }
        viewModelScope.launch {
            repo.isPolling.collect {
                _uiState.update { oldState ->
                    oldState.copy(isPolling = it)
                }
            }
        }
    }

    fun setQuery(query: String) {
        _uiState.update {
            it.copy(states = States.LOADING, query = query)
        }
        viewModelScope.launch {
            repo.setStoredQuery(query)
        }
    }

    fun changeToEmptyState() {
        _uiState.update {
            it.copy(states = States.EMPTY)
        }
    }

    fun togglePolling() {
        viewModelScope.launch {
            repo.setPolling(!_uiState.value.isPolling)
        }
    }

    private suspend fun fetchAllPhotos(): List<GalleryItem> =
        api.fetchPhotos().photos.galleryItems

    data class PhotoGalleryUiState(
        val images: List<GalleryItem> = listOf(),
        val query: String = "",
        val states: States = States.LOADING,
        val isPolling: Boolean = true
    )
}