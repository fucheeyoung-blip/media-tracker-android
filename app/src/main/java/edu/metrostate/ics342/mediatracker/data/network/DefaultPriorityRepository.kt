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
    // Sends a slimmed-down write shape (no nested `media`) since the API
    // rejected the full Priority object with a 400.
    suspend fun updatePriorities(priorities: List<Priority>): List<Priority> {
        val results = mutableListOf<Priority>()
        for (p in priorities) {
            val writeItem = PriorityWriteItem(
                mediaId = p.mediaId,
                priority = p.priority,
                orderIndex = p.orderIndex,
                estimatedTimeHours = p.estimatedTimeHours,
                notes = p.notes
            )
            val response = api.updatePriorities(writeItem)
            if (response.isSuccessful) {
                response.body()?.let { results.add(it) }
            } else {
                throw Exception("Something went wrong (${response.code()}).")
            }
        }
        // Fall back to what we just sent if the server ever omits a body,
        // so the UI still reflects the update even without full round-trip data.
        return if (results.size == priorities.size) results.sortedBy { it.orderIndex }
        else priorities.sortedBy { it.orderIndex }
    }
}