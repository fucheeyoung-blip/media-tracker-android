package edu.metrostate.ics342.mediatracker.data.network

import edu.metrostate.ics342.mediatracker.data.SessionRepository
import edu.metrostate.ics342.mediatracker.data.model.Favorite
import edu.metrostate.ics342.mediatracker.data.model.LibraryItem
import edu.metrostate.ics342.mediatracker.data.model.LibraryStatus
import edu.metrostate.ics342.mediatracker.data.model.Media
import edu.metrostate.ics342.mediatracker.data.model.Review

sealed class MediaDetailResult {
    data class Success(val media: Media) : MediaDetailResult()
    data class Error(val message: String) : MediaDetailResult()
}

class DefaultMediaDetailRepository(sessionRepository: SessionRepository) {

    private val api = RetrofitInstance.mediaApiService(sessionRepository)

    suspend fun getMedia(mediaId: Int): MediaDetailResult {
        return try {
            val response = api.getMedia(mediaId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) MediaDetailResult.Success(body)
                else MediaDetailResult.Error("No data returned.")
            } else if (response.code() == 404) {
                MediaDetailResult.Error("This item couldn't be found.")
            } else {
                MediaDetailResult.Error("Something went wrong (${response.code()}).")
            }
        } catch (e: Exception) {
            MediaDetailResult.Error(e.message ?: "Network error.")
        }
    }

    suspend fun getLibraryStatus(mediaId: Int): LibraryItem? {
        return try {
            val response = api.getLibraryStatus(mediaId)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getReviews(mediaId: Int): List<Review> {
        return try {
            val response = api.getReviews(mediaId)
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getFavoriteStatus(mediaId: Int): Favorite? {
        return try {
            val response = api.getFavoriteStatus(mediaId)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    // Throws on genuine failure so callers can roll back optimistic updates.
    // A 409 (already added/favorited) is treated as success, not a rollback trigger.
    suspend fun addToLibrary(mediaId: Int, status: LibraryStatus): LibraryItem {
        val response = api.addToLibrary(AddToLibraryRequest(mediaId, status.toApiString()))
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("No data returned.")
        }
        if (response.code() == 409) {
            return getLibraryStatus(mediaId) ?: throw Exception("Already added but couldn't confirm state.")
        }
        throw Exception("Something went wrong (${response.code()}).")
    }

    suspend fun addFavorite(mediaId: Int): Favorite {
        val response = api.addFavorite(AddFavoriteRequest(mediaId))
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("No data returned.")
        }
        if (response.code() == 409) {
            return getFavoriteStatus(mediaId) ?: throw Exception("Already favorited but couldn't confirm state.")
        }
        throw Exception("Something went wrong (${response.code()}).")
    }

    suspend fun removeFavorite(mediaId: Int) {
        val response = api.removeFavorite(mediaId)
        if (!response.isSuccessful && response.code() != 404) {
            // 404 means it's already gone — fine, treat as success.
            throw Exception("Something went wrong (${response.code()}).")
        }
    }
}