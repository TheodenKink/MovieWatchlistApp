package com.aris.moviewatchlist.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.aris.moviewatchlist.data.local.entity.WatchlistEntity
import com.aris.moviewatchlist.data.local.relations.WatchlistMovieCrossRef
import com.aris.moviewatchlist.data.local.relations.WatchlistWithMovies

@Dao
interface WatchlistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlist(watchlist: WatchlistEntity)

    @Update
    suspend fun updateWatchlist(watchlist: WatchlistEntity)

    @Delete
    suspend fun deleteWatchlist(watchlist: WatchlistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMovieToWatchlist(crossRef: WatchlistMovieCrossRef)

    @Delete
    suspend fun removeMovieFromWatchlist(crossRef: WatchlistMovieCrossRef)

    @Query("SELECT * FROM watchlists")
    fun getAllWatchlists(): LiveData<List<WatchlistEntity>>

    @Transaction
    @Query("SELECT * FROM watchlists WHERE watchlistId = :watchlistId")
    fun getWatchlistWithMovies(watchlistId: Int): LiveData<WatchlistWithMovies>

    @Transaction
    @Query("SELECT * FROM watchlists")
    fun getAllWatchlistsWithMovies(): LiveData<List<WatchlistWithMovies>>

    @Query("DELETE FROM watchlists")
    suspend fun deleteAllWatchlists()

    @Query("SELECT * FROM watchlists")
    suspend fun getAllWatchlistsOnce(): List<WatchlistEntity>

}
