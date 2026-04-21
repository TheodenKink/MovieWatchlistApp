package com.aris.moviewatchlist.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.aris.moviewatchlist.data.local.entity.MovieEntity

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity): Long

    @Update
    suspend fun updateMovie(movie: MovieEntity)

    @Delete
    suspend fun deleteMovie(movie: MovieEntity)

    @Query("SELECT * FROM movies")
    fun getAllMovies(): LiveData<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE isWatched = 1")
    fun getWatchedMovies(): LiveData<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE genre = :genre ORDER BY year DESC")
    fun getMoviesByGenre(genre: String): LiveData<List<MovieEntity>>

    @Query("SELECT COUNT(*) FROM movies WHERE isWatched = 0")
    suspend fun getPendingMovieCount(): Int

    @Query("DELETE FROM movies")
    suspend fun deleteAllMovies()
}
