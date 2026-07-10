package edu.metrostate.ics342.mediatracker.data.network

import edu.metrostate.ics342.mediatracker.data.SessionRepository
import edu.metrostate.ics342.mediatracker.data.model.LibraryItem
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
}