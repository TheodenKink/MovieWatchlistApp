package com.aris.moviewatchlist.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlists")
data class WatchlistEntity(
    @PrimaryKey(autoGenerate = true)
    val watchlistId: Int = 0,
    val name: String,
    val description: String? = null
)