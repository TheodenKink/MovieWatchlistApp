package com.aris.moviewatchlist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.aris.moviewatchlist.R
import com.aris.moviewatchlist.databinding.ActivityMainBinding
import com.aris.moviewatchlist.notifications.NotificationHelper
import com.aris.moviewatchlist.notifications.ReminderScheduler
import com.aris.moviewatchlist.ui.movies.AddMovieActivity
import com.aris.moviewatchlist.ui.movies.MovieAdapter
import com.aris.moviewatchlist.ui.movies.MovieViewModel
import com.aris.moviewatchlist.ui.queries.QueriesActivity
import com.aris.moviewatchlist.ui.reviews.ReviewsActivity
import com.aris.moviewatchlist.ui.watchlists.WatchlistsActivity

class MainActivity : AppCompatActivity() {

    private companion object {
        const val NOTIFICATION_PERMISSION_REQUEST_CODE = 2001
    }

    private lateinit var binding: ActivityMainBinding
    private val movieViewModel: MovieViewModel by viewModels()
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        setupNotifications()
        observeMovies()
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(
            onItemClick = { movie ->
                val intent = Intent(this, AddMovieActivity::class.java).apply {
                    putExtra("movieId", movie.movieId)
                    putExtra("title", movie.title)
                    putExtra("genre", movie.genre)
                    putExtra("year", movie.year)
                    putExtra("duration", movie.duration)
                    putExtra("platform", movie.platform)
                    putExtra("isWatched", movie.isWatched)
                    putExtra("personalRating", movie.personalRating ?: -1f)
                    putExtra("notes", movie.notes ?: "")
                    putExtra("posterUrl", movie.posterUrl)
                }
                startActivity(intent)
            }
        )

        binding.recyclerMovies.apply {
            layoutManager = GridLayoutManager(
                this@MainActivity,
                resources.getInteger(R.integer.movie_grid_span_count)
            )
            adapter = movieAdapter
        }
    }

    private fun setupListeners() {
        binding.fabAddMovie.setOnClickListener {
            startActivity(Intent(this, AddMovieActivity::class.java))
        }

        binding.btnWatchlists.setOnClickListener {
            startActivity(Intent(this, WatchlistsActivity::class.java))
        }

        binding.btnQueries.setOnClickListener {
            startActivity(Intent(this, QueriesActivity::class.java))
        }

        binding.btnReviews.setOnClickListener {
            startActivity(Intent(this, ReviewsActivity::class.java))
        }

        binding.btnTestReminder.setOnClickListener {
            if (ensureNotificationPermission()) {
                ReminderScheduler.runPendingMovieReminderNow(this)
                Toast.makeText(this, "Pending movie reminder queued", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeMovies() {
        movieViewModel.allMovies.observe(this) { movies ->
            movieAdapter.submitList(movies)
        }
    }

    private fun setupNotifications() {
        NotificationHelper.createNotificationChannel(this)
        ensureNotificationPermission()
        ReminderScheduler.scheduleDailyPendingMovieReminder(this)
    }

    private fun ensureNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }

        val isGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!isGranted) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }

        return isGranted
    }
}
