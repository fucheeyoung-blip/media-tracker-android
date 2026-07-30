package edu.metrostate.ics342.mediatracker.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Priority(
    val mediaId: Int,
    val priority: Int,
    val orderIndex: Int,
    val estimatedTimeHours: Double? = null,
    val notes: String? = null,
    val media: Media
)