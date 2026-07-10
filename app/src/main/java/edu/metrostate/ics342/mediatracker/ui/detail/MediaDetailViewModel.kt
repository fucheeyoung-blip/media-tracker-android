package edu.metrostate.ics342.mediatracker.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.metrostate.ics342.mediatracker.data.datastore.DefaultSessionRepository
import edu.metrostate.ics342.mediatracker.data.model.LibraryItem
import edu.metrostate.ics342.mediatracker.data.model.Media
import edu.metrostate.ics342.mediatracker.data.model.Review
import edu.metrostate.ics342.mediatracker.data.network.DefaultMediaDetailRepository
import edu.metrostate.ics342.mediatracker.data.network.MediaDetailResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MediaDetailUiState {
    object Loading : MediaDetailUiState()
    data class Error(val message: String) : MediaDetailUiState()
    data class Success(val media: Media, val libraryItem: LibraryItem?) : MediaDetailUiState()
}

class MediaDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionRepository = DefaultSessionRepository(application)
    private val repository = DefaultMediaDetailRepository(sessionRepository)

    private val _uiState = MutableStateFlow<MediaDetailUiState>(MediaDetailUiState.Loading)
    val uiState: StateFlow<MediaDetailUiState> = _uiState.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    // Guards against double-tapping "+ Want To" while a request is in flight.
    private val _isAddingToLibrary = MutableStateFlow(false)
    val isAddingToLibrary: StateFlow<Boolean> = _isAddingToLibrary.asStateFlow()

    private var currentMediaId: Int? = null

    fun setMediaId(id: Int) {
        // Avoid re-fetching if this screen is recomposed with the same id
        // (e.g. after a config change) — only load when it actually changes.
        if (currentMediaId == id) return
        currentMediaId = id
        loadMedia(id)
    }

    private fun loadMedia(id: Int) {
        _uiState.value = MediaDetailUiState.Loading
        viewModelScope.launch {
            when (val result = repository.getMedia(id)) {
                is MediaDetailResult.Success -> {
                    val libraryItem = repository.getLibraryStatus(id)
                    _uiState.value = MediaDetailUiState.Success(result.media, libraryItem)
                    _reviews.value = repository.getReviews(id)
                }
                is MediaDetailResult.Error -> {
                    _uiState.value = MediaDetailUiState.Error(result.message)
                }
            }
        }
    }

    fun retry() {
        currentMediaId?.let { loadMedia(it) }
    }

    fun onWantToTapped() {
        if (_isAddingToLibrary.value) return
        _isAddingToLibrary.value = true
        viewModelScope.launch {
            // real LibraryItem returned instead of just flipping this flag back.
            _isAddingToLibrary.value = false
        }
    }

    companion object {
        fun factory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MediaDetailViewModel(application) as T
                }
            }
    }
}