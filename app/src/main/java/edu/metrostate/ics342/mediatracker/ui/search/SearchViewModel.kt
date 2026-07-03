package edu.metrostate.ics342.mediatracker.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.metrostate.ics342.mediatracker.data.fakeSearchResults
import edu.metrostate.ics342.mediatracker.data.model.Media
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted

class SearchViewModel : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _allResults = MutableStateFlow<List<Media>>(emptyList())

    private val _selectedType = MutableStateFlow("")
    val selectedType: StateFlow<String> = _selectedType.asStateFlow()

    val popular: StateFlow<List<Media>> =
        combine(_allResults, _selectedType) { results, type ->
            if (type.isBlank()) results else results.filter { it.mediaType == type }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onQueryChange(value: String) { _query.value = value }

    fun onTypeSelect(type: String) { _selectedType.value = type }

    fun search(query: String) {
        _query.value = query
        _allResults.value = if (query.isBlank()) {
            listOf(
                fakeSearchResults.first { it.mediaType == "book" },
                fakeSearchResults.first { it.mediaType == "movie" },
                fakeSearchResults.first { it.mediaType == "show" }
            )
        } else {
            fakeSearchResults.filter {
                it.title.contains(query, ignoreCase = true)
            }
        }
    }

    init {
        viewModelScope.launch {
            _allResults.value = listOf(
                fakeSearchResults.first { it.mediaType == "book" },
                fakeSearchResults.first { it.mediaType == "movie" },
                fakeSearchResults.first { it.mediaType == "show" }
            )
        }
    }
}