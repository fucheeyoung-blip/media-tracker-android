package edu.metrostate.ics342.mediatracker.data.network

import edu.metrostate.ics342.mediatracker.data.SessionRepository
import edu.metrostate.ics342.mediatracker.data.model.Priority

sealed class PriorityResult {
    data class Success(val items: List<Priority>) : PriorityResult()
    data class Error(val message: String) : PriorityResult()
}

class DefaultPriorityRepository(sessionRepository: SessionRepository) {

    private val api = RetrofitInstance.mediaApiService(sessionRepository)

    suspend fun getPriorities(): PriorityResult {
        return try {
            val response = api.getPriorities()
            if (response.isSuccessful) {
                PriorityResult.Success(
                    (response.body() ?: emptyList()).sortedBy { it.orderIndex }
                )
            } else {
                PriorityResult.Error("Something went wrong (${response.code()}).")
            }
        } catch (e: Exception) {
            PriorityResult.Error(e.message ?: "Network error.")
        }
    }

    // Sends the FULL updated list — PUT /priorities replaces the whole set,
    // there's no per-item add/remove endpoint (per the handout).
    suspend fun updatePriorities(priorities: List<Priority>): List<Priority> {
        val response = api.updatePriorities(UpdatePrioritiesRequest(priorities))
        if (response.isSuccessful) {
            return (response.body() ?: emptyList()).sortedBy { it.orderIndex }
        }
        throw Exception("Something went wrong (${response.code()}).")
    }
}