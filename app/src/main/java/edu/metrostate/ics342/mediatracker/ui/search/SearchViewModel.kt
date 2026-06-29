package edu.metrostate.ics342.mediatracker.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.metrostate.ics342.mediatracker.data.fakeSearchResults
import edu.metrostate.ics342.mediatracker.data.model.Media
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _popular = MutableStateFlow<List<Media>>(emptyList())
    val popular: StateFlow<List<Media>> = _popular.asStateFlow()

    private val _selectedType = MutableStateFlow("")
    val selectedType: StateFlow<String> = _selectedType.asStateFlow()

    fun onQueryChange(value: String) { _query.value = value }

    fun onTypeSelect(type: String) { _selectedType.value = type }

    fun search(query: String) {
        _query.value = query
        _popular.value = if (query.isBlank()) {
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
            _popular.value = listOf(
                fakeSearchResults.first { it.mediaType == "book" },
                fakeSearchResults.first { it.mediaType == "movie" },
                fakeSearchResults.first { it.mediaType == "show" }
            )
        }
    }
}