package edu.metrostate.ics342.mediatracker.ui.detail

import androidx.lifecycle.ViewModel
import edu.metrostate.ics342.mediatracker.data.model.Media
import edu.metrostate.ics342.mediatracker.data.model.Review
import edu.metrostate.ics342.mediatracker.data.fakeSearchResults
import edu.metrostate.ics342.mediatracker.data.network.fakeReviews
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MediaDetailViewModel : ViewModel() {

    private val _media = MutableStateFlow<Media?>(null)
    val media: StateFlow<Media?> = _media.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    fun setMediaId(id: Int) {
        _media.value = fakeSearchResults.firstOrNull { it.id == id }
        _reviews.value = fakeReviews.filter { it.mediaId == id }
    }
}