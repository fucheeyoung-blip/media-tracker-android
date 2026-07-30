package edu.metrostate.ics342.mediatracker.data.model

data class Favorite(
    val userId: String,
    val mediaId: Int,
    val createdAt: String
)