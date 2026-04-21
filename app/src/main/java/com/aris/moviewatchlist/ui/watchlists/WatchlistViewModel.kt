package com.aris.moviewatchlist.ui.watchlists

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.aris.moviewatchlist.data.local.AppDatabase
import com.aris.moviewatchlist.data.local.entity.WatchlistEntity
import com.aris.moviewatchlist.data.local.relations.WatchlistMovieCrossRef
import com.aris.moviewatchlist.data.local.relations.WatchlistWithMovies
import com.aris.moviewatchlist.data.repository.WatchlistRepository
import kotlinx.coroutines.launch

class WatchlistViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WatchlistRepository
    val allWatchlists: LiveData<List<WatchlistEntity>>

    init {
        val watchlistDao = AppDatabase.getDatabase(application).watchlistDao()
        repository = WatchlistRepository(watchlistDao)
        allWatchlists = repository.getAllWatchlists()
    }

    fun insertWatchlist(watchlist: WatchlistEntity) {
        viewModelScope.launch {
            repository.insertWatchlist(watchlist)
        }
    }

    fun updateWatchlist(watchlist: WatchlistEntity) {
        viewModelScope.launch {
            repository.updateWatchlist(watchlist)
        }
    }

    fun deleteWatchlist(watchlist: WatchlistEntity) {
        viewModelScope.launch {
            repository.deleteWatchlist(watchlist)
        }
    }

    fun addMovieToWatchlist(crossRef: WatchlistMovieCrossRef) {
        viewModelScope.launch {
            repository.addMovieToWatchlist(crossRef)
        }
    }

    suspend fun addMovieToWatchlistAndWait(crossRef: WatchlistMovieCrossRef) {
        repository.addMovieToWatchlist(crossRef)
    }

    fun removeMovieFromWatchlist(crossRef: WatchlistMovieCrossRef) {
        viewModelScope.launch {
            repository.removeMovieFromWatchlist(crossRef)
        }
    }

    fun getWatchlistWithMovies(watchlistId: Int): LiveData<WatchlistWithMovies> {
        return repository.getWatchlistWithMovies(watchlistId)
    }

    fun getAllWatchlistsWithMovies(): LiveData<List<WatchlistWithMovies>> {
        return repository.getAllWatchlistsWithMovies()
    }

    fun deleteAllWatchlists() {
        viewModelScope.launch {
            repository.deleteAllWatchlists()
        }
    }

    suspend fun getAllWatchlistsOnce(): List<WatchlistEntity> {
        return repository.getAllWatchlistsOnce()
    }
}
