package edu.metrostate.ics342.mediatracker.data.network

import edu.metrostate.ics342.mediatracker.data.SessionRepository
import edu.metrostate.ics342.mediatracker.data.model.LibraryItem
import edu.metrostate.ics342.mediatracker.data.model.LibraryStatus

sealed class LibraryResult {
    data class Success(val items: List<LibraryItem>) : LibraryResult()
    data class Error(val message: String) : LibraryResult()
}

class DefaultLibraryRepository(sessionRepository: SessionRepository) {

    private val api = RetrofitInstance.mediaApiService(sessionRepository)

    suspend fun getLibrary(status: LibraryStatus?): LibraryResult {
        return try {
            val response = api.getLibrary(status?.toApiString())
            if (response.isSuccessful) {
                LibraryResult.Success(response.body() ?: emptyList())
            } else {
                LibraryResult.Error("Something went wrong (${response.code()}).")
            }
        } catch (e: Exception) {
            LibraryResult.Error(e.message ?: "Network error.")
        }
    }

    // Throws on real failure so the ViewModel can roll back the optimistic removal.
    suspend fun removeFromLibrary(mediaId: Int) {
        val response = api.removeFromLibrary(mediaId)
        if (!response.isSuccessful && response.code() != 404) {
            throw Exception("Something went wrong (${response.code()}).")
        }
    }

    suspend fun updateStatus(mediaId: Int, newStatus: LibraryStatus): LibraryItem {
        val response = api.updateLibraryStatus(mediaId, UpdateLibraryStatusRequest(newStatus.toApiString()))
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("No data returned.")
        }
        throw Exception("Something went wrong (${response.code()}).")
    }
}