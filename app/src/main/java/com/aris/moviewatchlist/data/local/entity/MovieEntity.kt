package com.aris.moviewatchlist.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey(autoGenerate = true)
    val movieId: Int = 0,
    val title: String,
    val genre: String,
    val year: Int,
    val duration: Int,
    val platform: String,
    val isWatched: Boolean = false,
    val personalRating: Float? = null,
    val notes: String? = null
)