package com.aris.moviewatchlist.ui.movies

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.aris.moviewatchlist.data.local.AppDatabase
import com.aris.moviewatchlist.data.local.entity.MovieEntity
import com.aris.moviewatchlist.data.repository.MovieRepository
import kotlinx.coroutines.launch

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MovieRepository
    val allMovies: LiveData<List<MovieEntity>>

    init {
        val movieDao = AppDatabase.getDatabase(application).movieDao()
        repository = MovieRepository(movieDao)
        allMovies = repository.getAllMovies()
    }

    fun insertMovie(movie: MovieEntity) {
        viewModelScope.launch {
            repository.insertMovie(movie)
        }
    }

    suspend fun insertMovieAndReturnId(movie: MovieEntity): Long {
        return repository.insertMovie(movie)
    }

    fun updateMovie(movie: MovieEntity) {
        viewModelScope.launch {
            repository.updateMovie(movie)
        }
    }

    suspend fun updateMovieAndWait(movie: MovieEntity) {
        repository.updateMovie(movie)
    }

    fun deleteMovie(movie: MovieEntity) {
        viewModelScope.launch {
            repository.deleteMovie(movie)
        }
    }

    fun getWatchedMovies(): LiveData<List<MovieEntity>> {
        return repository.getWatchedMovies()
    }

    fun getMoviesByGenre(genre: String): LiveData<List<MovieEntity>> {
        return repository.getMoviesByGenre(genre)
    }

    fun deleteAllMovies() {
        viewModelScope.launch {
            repository.deleteAllMovies()
        }
    }
}
