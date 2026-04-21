package com.aris.moviewatchlist.data.repository

import androidx.lifecycle.LiveData
import com.aris.moviewatchlist.data.local.dao.WatchlistDao
import com.aris.moviewatchlist.data.local.entity.WatchlistEntity
import com.aris.moviewatchlist.data.local.relations.WatchlistMovieCrossRef
import com.aris.moviewatchlist.data.local.relations.WatchlistWithMovies

class WatchlistRepository(private val watchlistDao: WatchlistDao) {

    fun getAllWatchlists(): LiveData<List<WatchlistEntity>> {
        return watchlistDao.getAllWatchlists()
    }

    fun getWatchlistWithMovies(watchlistId: Int): LiveData<WatchlistWithMovies> {
        return watchlistDao.getWatchlistWithMovies(watchlistId)
    }

    fun getAllWatchlistsWithMovies(): LiveData<List<WatchlistWithMovies>> {
        return watchlistDao.getAllWatchlistsWithMovies()
    }

    suspend fun insertWatchlist(watchlist: WatchlistEntity) {
        watchlistDao.insertWatchlist(watchlist)
    }

    suspend fun updateWatchlist(watchlist: WatchlistEntity) {
        watchlistDao.updateWatchlist(watchlist)
    }

    suspend fun deleteWatchlist(watchlist: WatchlistEntity) {
        watchlistDao.deleteWatchlist(watchlist)
    }

    suspend fun addMovieToWatchlist(crossRef: WatchlistMovieCrossRef) {
        watchlistDao.addMovieToWatchlist(crossRef)
    }

    suspend fun removeMovieFromWatchlist(crossRef: WatchlistMovieCrossRef) {
        watchlistDao.removeMovieFromWatchlist(crossRef)
    }

    suspend fun deleteAllWatchlists() {
        watchlistDao.deleteAllWatchlists()
    }

    suspend fun getAllWatchlistsOnce(): List<WatchlistEntity> {
        return watchlistDao.getAllWatchlistsOnce()
    }
}