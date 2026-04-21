package com.aris.moviewatchlist.data.local.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.aris.moviewatchlist.data.local.entity.MovieEntity
import com.aris.moviewatchlist.data.local.entity.WatchlistEntity

data class WatchlistWithMovies(
    @Embedded
    val watchlist: WatchlistEntity,

    @Relation(
        parentColumn = "watchlistId",
        entityColumn = "movieId",
        associateBy = Junction(
            value = WatchlistMovieCrossRef::class,
            parentColumn = "watchlistId",
            entityColumn = "movieId"
        )
    )
    val movies: List<MovieEntity>
)