package edu.metrostate.ics342.mediatracker.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.metrostate.ics342.mediatracker.data.datastore.DefaultSessionRepository
import edu.metrostate.ics342.mediatracker.data.model.LibraryItem
import edu.metrostate.ics342.mediatracker.data.model.LibraryStatus
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
    data class Success(
        val media: Media,
        val libraryItem: LibraryItem?,
        val isFavorited: Boolean
    ) : MediaDetailUiState()
}

class MediaDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionRepository = DefaultSessionRepository(application)
    private val repository = DefaultMediaDetailRepository(sessionRepository)

    private val _uiState = MutableStateFlow<MediaDetailUiState>(MediaDetailUiState.Loading)
    val uiState: StateFlow<MediaDetailUiState> = _uiState.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // No longer needed to block taps for the network round-trip — kept only
    // to prevent double-firing before the optimistic state has even updated.
    private val _isAddingToLibrary = MutableStateFlow(false)
    val isAddingToLibrary: StateFlow<Boolean> = _isAddingToLibrary.asStateFlow()

    private val _isSavingFavorite = MutableStateFlow(false)
    val isSavingFavorite: StateFlow<Boolean> = _isSavingFavorite.asStateFlow()

    private var currentMediaId: Int? = null

    fun setMediaId(id: Int) {
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
                    val favorite = repository.getFavoriteStatus(id)
                    _uiState.value = MediaDetailUiState.Success(
                        media = result.media,
                        libraryItem = libraryItem,
                        isFavorited = favorite != null
                    )
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

    fun clearError() {
        _errorMessage.value = null
    }

    // Optimistic: mark as added immediately, roll back only if the call genuinely fails.
    fun onWantToTapped() {
        val id = currentMediaId ?: return
        val current = _uiState.value
        if (current !is MediaDetailUiState.Success) return
        if (current.libraryItem != null) return // already in library, nothing to do

        // A placeholder LibraryItem so the button flips instantly.
        // Real fields (addedAt/updatedAt/media) get corrected once the network call returns.
        val optimisticItem = LibraryItem(
            userId = "",
            mediaId = id,
            status = LibraryStatus.WANT_TO,
            addedAt = "",
            updatedAt = "",
            media = current.media
        )

        _uiState.value = current.copy(libraryItem = optimisticItem)

        viewModelScope.launch {
            try {
                val realItem = repository.addToLibrary(id, LibraryStatus.WANT_TO)
                val latest = _uiState.value
                if (latest is MediaDetailUiState.Success) {
                    _uiState.value = latest.copy(libraryItem = realItem)
                }
            } catch (e: Exception) {
                val latest = _uiState.value
                if (latest is MediaDetailUiState.Success) {
                    _uiState.value = latest.copy(libraryItem = null) // roll back
                }
                _errorMessage.value = "Couldn't add to library. Try again."
            }
        }
    }

    // Optimistic toggle: covers both directions (favorite <-> unfavorite).
    fun onSaveTapped() {
        val id = currentMediaId ?: return
        val current = _uiState.value
        if (current !is MediaDetailUiState.Success) return

        val wasFavorited = current.isFavorited
        _uiState.value = current.copy(isFavorited = !wasFavorited) // flip immediately

        viewModelScope.launch {
            try {
                if (wasFavorited) {
                    repository.removeFavorite(id)
                } else {
                    repository.addFavorite(id)
                }
                // success — optimistic state was already correct, nothing more to do
            } catch (e: Exception) {
                val latest = _uiState.value
                if (latest is MediaDetailUiState.Success) {
                    _uiState.value = latest.copy(isFavorited = wasFavorited) // roll back
                }
                _errorMessage.value = "Couldn't update favorite. Try again."
            }
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