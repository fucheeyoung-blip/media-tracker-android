package edu.metrostate.ics342.mediatracker.ui.library

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.metrostate.ics342.mediatracker.data.datastore.DefaultSessionRepository
import edu.metrostate.ics342.mediatracker.data.model.LibraryItem
import edu.metrostate.ics342.mediatracker.data.model.LibraryStatus
import edu.metrostate.ics342.mediatracker.data.network.DefaultLibraryRepository
import edu.metrostate.ics342.mediatracker.data.network.LibraryResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LibraryUiState {
    object Loading : LibraryUiState()
    data class Error(val message: String) : LibraryUiState()
    data class Success(val items: List<LibraryItem>) : LibraryUiState()
}

open class LibraryViewModel(
    application: Application,
    private val repository: DefaultLibraryRepository = DefaultLibraryRepository(
        DefaultSessionRepository(application)
    )
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<LibraryUiState>(LibraryUiState.Loading)
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    private val _filterState = MutableStateFlow(LibraryStatus.WANT_TO)
    val filterState: StateFlow<LibraryStatus> = _filterState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadLibrary()
    }

    fun loadLibrary() {
        viewModelScope.launch {
            _uiState.value = LibraryUiState.Loading
            when (val result = repository.getLibrary(_filterState.value)) {
                is LibraryResult.Success -> _uiState.value = LibraryUiState.Success(result.items)
                is LibraryResult.Error -> _uiState.value = LibraryUiState.Error(result.message)
            }
        }
    }

    fun updateFilter(status: LibraryStatus) {
        if (_filterState.value == status) return
        _filterState.value = status
        loadLibrary()
    }

    fun clearError() {
        _errorMessage.value = null
    }

    // Optimistic remove: item disappears instantly, rolled back only on genuine failure.
    open fun removeItem(mediaId: Int) {
        val current = _uiState.value
        if (current !is LibraryUiState.Success) return

        val backup = current.items.find { it.mediaId == mediaId }
        _uiState.value = current.copy(items = current.items.filter { it.mediaId != mediaId })

        viewModelScope.launch {
            try {
                repository.removeFromLibrary(mediaId)
            } catch (e: Exception) {
                val latest = _uiState.value
                if (latest is LibraryUiState.Success && backup != null) {
                    _uiState.value = latest.copy(items = latest.items + backup)
                }
                _errorMessage.value = "Couldn't remove item. Try again."
            }
        }
    }

    // Optimistic status change. If the new status differs from the current filter tab,
    // the item also visually leaves this list right away (matches segmented-control filtering).
    open fun updateStatus(mediaId: Int, newStatus: LibraryStatus) {
        val current = _uiState.value
        if (current !is LibraryUiState.Success) return

        val backup = current.items.find { it.mediaId == mediaId } ?: return
        val updatedItem = backup.copy(status = newStatus)

        val newItems = if (newStatus == _filterState.value) {
            current.items.map { if (it.mediaId == mediaId) updatedItem else it }
        } else {
            current.items.filter { it.mediaId != mediaId } // moved out of this tab's view
        }
        _uiState.value = current.copy(items = newItems)

        viewModelScope.launch {
            try {
                repository.updateStatus(mediaId, newStatus)
            } catch (e: Exception) {
                val latest = _uiState.value
                if (latest is LibraryUiState.Success) {
                    val restored = if (latest.items.any { it.mediaId == mediaId }) {
                        latest.items.map { if (it.mediaId == mediaId) backup else it }
                    } else {
                        latest.items + backup
                    }
                    _uiState.value = latest.copy(items = restored)
                }
                _errorMessage.value = "Couldn't update status. Try again."
            }
        }
    }

    companion object {
        fun factory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LibraryViewModel(application) as T
                }
            }
    }
}