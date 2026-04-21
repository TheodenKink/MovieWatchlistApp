package com.aris.moviewatchlist.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aris.moviewatchlist.data.local.dao.MovieDao
import com.aris.moviewatchlist.data.local.dao.WatchlistDao
import com.aris.moviewatchlist.data.local.entity.MovieEntity
import com.aris.moviewatchlist.data.local.entity.WatchlistEntity
import com.aris.moviewatchlist.data.local.relations.WatchlistMovieCrossRef

@Database(
    entities = [
        MovieEntity::class,
        WatchlistEntity::class,
        WatchlistMovieCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao
    abstract fun watchlistDao(): WatchlistDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "movie_watchlist_db"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}