package edu.metrostate.ics342.mediatracker.data.network

import edu.metrostate.ics342.mediatracker.data.model.Review

val fakeReviews = listOf(
    Review(
        userId = "user-101",
        mediaId = 1,
        rating = 5,
        reviewText = "A timeless classic. Fresh every time.",
        createdAt = "2024-01-20T10:00:00Z"
    ),
    Review(
        userId = "user-102",
        mediaId = 1,
        rating = 4,
        reviewText = "Great world-building, slow in the middle.",
        createdAt = "2024-01-13T10:00:00Z"
    ),
    Review(
        userId = "user-103",
        mediaId = 21,
        rating = 5,
        reviewText = "Villeneuve at his best. Stunning visuals.",
        createdAt = "2024-01-19T10:00:00Z"
    ),
    Review(
        userId = "user-104",
        mediaId = 21,
        rating = 4,
        reviewText = "Thought-provoking but slow burn.",
        createdAt = "2024-01-17T10:00:00Z"
    ),
    Review(
        userId = "user-105",
        mediaId = 41,
        rating = 5,
        reviewText = "Best show of the decade. Period.",
        createdAt = "2024-01-21T10:00:00Z"
    ),
    Review(
        userId = "user-106",
        mediaId = 41,
        rating = 4,
        reviewText = "Unsettling and brilliant.",
        createdAt = "2024-01-18T10:00:00Z"
    ),
)