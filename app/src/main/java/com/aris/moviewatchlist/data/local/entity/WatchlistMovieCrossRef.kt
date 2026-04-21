package com.aris.moviewatchlist.data.local.relations

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.aris.moviewatchlist.data.local.entity.MovieEntity
import com.aris.moviewatchlist.data.local.entity.WatchlistEntity

@Entity(
    tableName = "watchlist_movies",
    primaryKeys = ["watchlistId", "movieId"],
    foreignKeys = [
        ForeignKey(
            entity = WatchlistEntity::class,
            parentColumns = ["watchlistId"],
            childColumns = ["watchlistId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["movieId"],
            childColumns = ["movieId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("watchlistId"), Index("movieId")]
)
data class WatchlistMovieCrossRef(
    val watchlistId: Int,
    val movieId: Int,
    val dateAdded: Long = System.currentTimeMillis()
)