package com.aris.moviewatchlist.ui.movies

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aris.moviewatchlist.data.local.entity.MovieEntity
import com.aris.moviewatchlist.data.local.relations.WatchlistMovieCrossRef
import com.aris.moviewatchlist.databinding.ActivityAddMovieBinding
import com.aris.moviewatchlist.ui.watchlists.WatchlistViewModel
import kotlinx.coroutines.launch

class AddMovieActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMovieBinding
    private val movieViewModel: MovieViewModel by viewModels()
    private val watchlistViewModel: WatchlistViewModel by viewModels()

    private var movieId: Int = 0
    private var isEditMode: Boolean = false
    private var existingRating: Float? = null
    private var existingNotes: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        readIntentData()

        binding.btnSave.setOnClickListener {
            saveMovie()
        }

        binding.btnDelete.setOnClickListener {
            showDeleteConfirmation()
        }

        binding.btnAddToWatchlist.setOnClickListener {
            showAddToWatchlistDialog()
        }
    }

    private fun readIntentData() {
        if (intent.hasExtra("movieId")) {
            isEditMode = true

            movieId = intent.getIntExtra("movieId", 0)
            val title = intent.getStringExtra("title").orEmpty()
            val genre = intent.getStringExtra("genre").orEmpty()
            val year = intent.getIntExtra("year", 0)
            val duration = intent.getIntExtra("duration", 0)
            val platform = intent.getStringExtra("platform").orEmpty()
            val isWatched = intent.getBooleanExtra("isWatched", false)

            val ratingValue = intent.getFloatExtra("personalRating", -1f)
            existingRating = if (ratingValue >= 0f) ratingValue else null

            existingNotes = intent.getStringExtra("notes")

            binding.tvFormTitle.text = "Edit Movie"
            binding.btnSave.text = "Update Movie"
            binding.btnDelete.visibility = View.VISIBLE
            binding.btnAddToWatchlist.visibility = View.VISIBLE

            binding.etTitle.setText(title)
            binding.etGenre.setText(genre)
            binding.etYear.setText(year.toString())
            binding.etDuration.setText(duration.toString())
            binding.etPlatform.setText(platform)
            binding.cbWatched.isChecked = isWatched
        } else {
            binding.tvFormTitle.text = "Add Movie"
            binding.btnSave.text = "Save Movie"
            binding.btnDelete.visibility = View.GONE
            binding.btnAddToWatchlist.visibility = View.GONE
            binding.cbWatched.isChecked = false
        }
    }

    private fun saveMovie() {
        val title = binding.etTitle.text.toString().trim()
        val genre = binding.etGenre.text.toString().trim()
        val year = binding.etYear.text.toString().toIntOrNull() ?: 0
        val duration = binding.etDuration.text.toString().toIntOrNull() ?: 0
        val platform = binding.etPlatform.text.toString().trim()
        val isWatched = binding.cbWatched.isChecked

        if (title.isNotEmpty()) {
            val movie = MovieEntity(
                movieId = if (isEditMode) movieId else 0,
                title = title,
                genre = genre,
                year = year,
                duration = duration,
                platform = platform,
                isWatched = isWatched,
                personalRating = existingRating,
                notes = existingNotes
            )

            if (isEditMode) {
                movieViewModel.updateMovie(movie)
            } else {
                movieViewModel.insertMovie(movie)
            }

            finish()
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Movie")
            .setMessage("Are you sure you want to delete this movie?")
            .setPositiveButton("Delete") { _, _ ->
                deleteMovie()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteMovie() {
        val movie = MovieEntity(
            movieId = movieId,
            title = binding.etTitle.text.toString().trim(),
            genre = binding.etGenre.text.toString().trim(),
            year = binding.etYear.text.toString().toIntOrNull() ?: 0,
            duration = binding.etDuration.text.toString().toIntOrNull() ?: 0,
            platform = binding.etPlatform.text.toString().trim(),
            isWatched = binding.cbWatched.isChecked,
            personalRating = existingRating,
            notes = existingNotes
        )

        movieViewModel.deleteMovie(movie)
        finish()
    }

    private fun showAddToWatchlistDialog() {
        lifecycleScope.launch {
            val watchlists = watchlistViewModel.getAllWatchlistsOnce()

            if (watchlists.isEmpty()) {
                AlertDialog.Builder(this@AddMovieActivity)
                    .setTitle("No Watchlists")
                    .setMessage("Create a watchlist first.")
                    .setPositiveButton("OK", null)
                    .show()
                return@launch
            }

            val watchlistNames = watchlists.map { it.name }.toTypedArray()

            AlertDialog.Builder(this@AddMovieActivity)
                .setTitle("Add to Watchlist")
                .setItems(watchlistNames) { _, which ->
                    val selectedWatchlist = watchlists[which]

                    lifecycleScope.launch {
                        watchlistViewModel.addMovieToWatchlistAndWait(
                            WatchlistMovieCrossRef(
                                watchlistId = selectedWatchlist.watchlistId,
                                movieId = movieId
                            )
                        )

                        Toast.makeText(
                            this@AddMovieActivity,
                            "Added to ${selectedWatchlist.name}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}
