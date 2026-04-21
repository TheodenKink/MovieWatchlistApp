package com.aris.moviewatchlist.data.repository

import androidx.lifecycle.LiveData
import com.aris.moviewatchlist.data.local.dao.MovieDao
import com.aris.moviewatchlist.data.local.entity.MovieEntity

class MovieRepository(private val movieDao: MovieDao) {

    fun getAllMovies(): LiveData<List<MovieEntity>> {
        return movieDao.getAllMovies()
    }

    fun getWatchedMovies(): LiveData<List<MovieEntity>> {
        return movieDao.getWatchedMovies()
    }

    fun getMoviesByGenre(genre: String): LiveData<List<MovieEntity>> {
        return movieDao.getMoviesByGenre(genre)
    }

    suspend fun insertMovie(movie: MovieEntity): Long {
        return movieDao.insertMovie(movie)
    }

    suspend fun updateMovie(movie: MovieEntity) {
        movieDao.updateMovie(movie)
    }

    suspend fun deleteMovie(movie: MovieEntity) {
        movieDao.deleteMovie(movie)
    }

    suspend fun deleteAllMovies() {
        movieDao.deleteAllMovies()
    }
}
