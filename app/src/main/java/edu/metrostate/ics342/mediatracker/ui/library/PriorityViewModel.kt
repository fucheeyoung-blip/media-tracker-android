package edu.metrostate.ics342.mediatracker.ui.library

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.metrostate.ics342.mediatracker.data.datastore.DefaultSessionRepository
import edu.metrostate.ics342.mediatracker.data.model.Priority
import edu.metrostate.ics342.mediatracker.data.network.DefaultPriorityRepository
import edu.metrostate.ics342.mediatracker.data.network.PriorityResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

const val MAX_PRIORITIES = 5

sealed class PriorityUiState {
    object Loading : PriorityUiState()
    data class Error(val message: String) : PriorityUiState()
    data class Success(val items: List<Priority>) : PriorityUiState()
}

open class PriorityViewModel(
    application: Application,
    private val repository: DefaultPriorityRepository = DefaultPriorityRepository(
        DefaultSessionRepository(application)
    )
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<PriorityUiState>(PriorityUiState.Loading)
    val uiState: StateFlow<PriorityUiState> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadPriorities()
    }

    fun loadPriorities() {
        viewModelScope.launch {
            _uiState.value = PriorityUiState.Loading
            when (val result = repository.getPriorities()) {
                is PriorityResult.Success -> _uiState.value = PriorityUiState.Success(result.items)
                is PriorityResult.Error -> _uiState.value = PriorityUiState.Error(result.message)
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    // Returns true if a new item can still be added (client-side 5-item cap,
    // per the handout — don't just wait for the server's 400).
    fun canAddMore(): Boolean {
        val current = _uiState.value
        return current !is PriorityUiState.Success || current.items.size < MAX_PRIORITIES
    }

    // Adds a new priority item or updates an existing one (same mediaId), then
    // PUTs the FULL list — there's no per-item endpoint, per the handout.
    // Optimistic: local state updates immediately, rolled back on genuine failure.
    open fun addOrUpdatePriority(item: Priority) {
        val current = _uiState.value
        val currentItems = if (current is PriorityUiState.Success) current.items else emptyList()

        val isNew = currentItems.none { it.mediaId == item.mediaId }
        if (isNew && currentItems.size >= MAX_PRIORITIES) {
            _errorMessage.value = "You can only prioritize up to $MAX_PRIORITIES items."
            return
        }

        val newItems = if (isNew) {
            currentItems + item
        } else {
            currentItems.map { if (it.mediaId == item.mediaId) item else it }
        }.sortedBy { it.orderIndex }

        val backup = currentItems
        _uiState.value = PriorityUiState.Success(newItems)

        viewModelScope.launch {
            try {
                val updated = repository.updatePriorities(newItems)
                _uiState.value = PriorityUiState.Success(updated)
            } catch (e: Exception) {
                _uiState.value = PriorityUiState.Success(backup)
                _errorMessage.value = "Couldn't update priorities. Try again."
            }
        }
    }

    // Removes an item from the priority list. No DELETE endpoint exists —
    // PUT a new list without the removed item, per the handout.
    open fun removePriority(mediaId: Int) {
        val current = _uiState.value
        if (current !is PriorityUiState.Success) return

        val backup = current.items
        val newItems = current.items.filter { it.mediaId != mediaId }
        _uiState.value = PriorityUiState.Success(newItems)

        viewModelScope.launch {
            try {
                val updated = repository.updatePriorities(newItems)
                _uiState.value = PriorityUiState.Success(updated)
            } catch (e: Exception) {
                _uiState.value = PriorityUiState.Success(backup)
                _errorMessage.value = "Couldn't remove item. Try again."
            }
        }
    }

    companion object {
        fun factory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PriorityViewModel(application) as T
                }
            }
    }
}