package edu.metrostate.ics342.mediatracker.data.network

import edu.metrostate.ics342.mediatracker.data.SessionRepository
import edu.metrostate.ics342.mediatracker.data.model.Media

sealed class SearchResult {
    data class Success(val items: List<Media>) : SearchResult()
    data class Error(val message: String) : SearchResult()
}

class DefaultSearchRepository(sessionRepository: SessionRepository) {

    private val api = RetrofitInstance.mediaApiService(sessionRepository)

    suspend fun search(query: String, type: String? = null): SearchResult {
        return try {
            val response = api.searchMedia(
                query = query.ifBlank { null },
                type = type?.ifBlank { null }
            )
            if (response.isSuccessful) {
                SearchResult.Success(response.body() ?: emptyList())
            } else {
                SearchResult.Error("Something went wrong (${response.code()}).")
            }
        } catch (e: Exception) {
            SearchResult.Error(e.message ?: "Network error.")
        }
    }
}