package edu.metrostate.ics342.mediatracker.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.metrostate.ics342.mediatracker.data.datastore.DefaultSessionRepository
import edu.metrostate.ics342.mediatracker.data.model.Media
import edu.metrostate.ics342.mediatracker.data.network.DefaultSearchRepository
import edu.metrostate.ics342.mediatracker.data.network.SearchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchResultsViewModel(
    application: Application,
    private val repository: DefaultSearchRepository = DefaultSearchRepository(
        DefaultSessionRepository(application)
    )
) : AndroidViewModel(application) {

    private val _results = MutableStateFlow<List<Media>>(emptyList())
    val results: StateFlow<List<Media>> = _results.asStateFlow()

    private val _selectedType = MutableStateFlow("")
    val selectedType: StateFlow<String> = _selectedType.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var currentQuery = ""

    fun search(query: String) {
        currentQuery = query
        runSearch()
    }

    fun onTypeSelect(type: String) {
        _selectedType.value = type
        runSearch()
    }

    private fun runSearch() {
        val query = currentQuery
        val type = _selectedType.value

        viewModelScope.launch {
            when (val result = repository.search(query, type.ifBlank { null })) {
                is SearchResult.Success -> _results.value = result.items
                is SearchResult.Error -> _errorMessage.value = result.message
            }
        }
    }

    companion object {
        fun factory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchResultsViewModel(application) as T
                }
            }
    }
}