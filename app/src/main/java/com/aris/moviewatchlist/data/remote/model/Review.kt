package com.aris.moviewatchlist.data.remote.model

data class Review(
    val reviewId: String = "",
    val movieTitle: String = "",
    val username: String = "",
    val rating: Double = 0.0,
    val comment: String = "",
    val dateWatched: String = ""
)
