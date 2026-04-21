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
import com.aris.moviewatchlist.data.remote.tmdb.TmdbMovieMetadata
import com.aris.moviewatchlist.data.remote.tmdb.TmdbMovieRepository
import com.aris.moviewatchlist.data.remote.tmdb.TmdbMovieSearchCandidate
import com.aris.moviewatchlist.databinding.ActivityAddMovieBinding
import com.aris.moviewatchlist.ui.reviews.ReviewsViewModel
import com.aris.moviewatchlist.ui.watchlists.WatchlistViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

class AddMovieActivity : AppCompatActivity() {

    private companion object {
        const val MAX_MOVIE_SELECTION_RESULTS = 8
    }

    private lateinit var binding: ActivityAddMovieBinding
    private val movieViewModel: MovieViewModel by viewModels()
    private val watchlistViewModel: WatchlistViewModel by viewModels()
    private val reviewsViewModel: ReviewsViewModel by viewModels()

    private var movieId: Int = 0
    private var isEditMode: Boolean = false
    private var originalTitle: String = ""
    private var existingRating: Float? = null
    private var existingNotes: String? = null
    private var existingPosterUrl: String? = null
    private val tmdbMovieRepository = TmdbMovieRepository()

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

        binding.ratingBarMovie.setOnRatingBarChangeListener { _, rating, _ ->
            updateRatingLabel(rating)
        }

        binding.btnClearRating.setOnClickListener {
            binding.ratingBarMovie.rating = 0f
        }
    }

    private fun readIntentData() {
        if (intent.hasExtra("movieId")) {
            isEditMode = true

            movieId = intent.getIntExtra("movieId", 0)
            val title = intent.getStringExtra("title").orEmpty()
            originalTitle = title
            val genre = intent.getStringExtra("genre").orEmpty()
            val year = intent.getIntExtra("year", 0)
            val duration = intent.getIntExtra("duration", 0)
            val platform = intent.getStringExtra("platform").orEmpty()
            val isWatched = intent.getBooleanExtra("isWatched", false)

            val ratingValue = intent.getFloatExtra("personalRating", -1f)
            existingRating = if (ratingValue >= 0f) ratingValue else null

            existingNotes = intent.getStringExtra("notes")
            existingPosterUrl = intent.getStringExtra("posterUrl")

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
            binding.ratingBarMovie.rating = existingRating ?: 0f
            updateRatingLabel(binding.ratingBarMovie.rating)
        } else {
            binding.tvFormTitle.text = "Add Movie"
            binding.btnSave.text = "Save Movie"
            binding.btnDelete.visibility = View.GONE
            binding.btnAddToWatchlist.visibility = View.GONE
            binding.cbWatched.isChecked = false
            updateRatingLabel(0f)
        }
    }

    private fun saveMovie() {
        val title = binding.etTitle.text.toString().trim()

        if (title.isNotEmpty()) {
            lifecycleScope.launch {
                binding.btnSave.isEnabled = false

                val candidates = tmdbMovieRepository.searchMovies(title)
                if (candidates.isEmpty()) {
                    saveMovieWithMetadata(null)
                } else if (candidates.size == 1) {
                    val metadata = tmdbMovieRepository.findMovieMetadata(candidates.first().id)
                    saveMovieWithMetadata(metadata)
                } else {
                    binding.btnSave.isEnabled = true
                    showMovieSelectionDialog(candidates.take(MAX_MOVIE_SELECTION_RESULTS))
                }
            }
        }
    }

    private fun showMovieSelectionDialog(candidates: List<TmdbMovieSearchCandidate>) {
        val labels = candidates.map { candidate ->
            if (candidate.year > 0) "${candidate.title} (${candidate.year})" else candidate.title
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Select movie")
            .setItems(labels) { _, which ->
                val selectedMovie = candidates[which]
                lifecycleScope.launch {
                    binding.btnSave.isEnabled = false
                    val metadata = tmdbMovieRepository.findMovieMetadata(selectedMovie.id)
                    saveMovieWithMetadata(metadata)
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                binding.btnSave.isEnabled = true
            }
            .setNeutralButton("Save manually") { _, _ ->
                saveMovieWithMetadata(null)
            }
            .show()
    }

    private fun saveMovieWithMetadata(metadata: TmdbMovieMetadata?) {
        val title = binding.etTitle.text.toString().trim()
        val enteredGenre = binding.etGenre.text.toString().trim()
        val enteredYear = binding.etYear.text.toString().toIntOrNull() ?: 0
        val enteredDuration = binding.etDuration.text.toString().toIntOrNull() ?: 0
        val platform = binding.etPlatform.text.toString().trim()
        val isWatched = binding.cbWatched.isChecked
        val rating = binding.ratingBarMovie.rating.takeIf { it > 0f }
        val titleChanged = title.normalizeMovieTitle() != originalTitle.normalizeMovieTitle()
        val posterUrl = metadata?.posterUrl ?: if (titleChanged) null else existingPosterUrl

        val movie = MovieEntity(
            movieId = if (isEditMode) movieId else 0,
            title = metadata?.title ?: title,
            genre = enteredGenre.ifBlank { metadata?.genre.orEmpty() },
            year = if (enteredYear > 0) enteredYear else metadata?.year ?: 0,
            duration = if (enteredDuration > 0) enteredDuration else metadata?.duration ?: 0,
            platform = platform,
            isWatched = isWatched,
            personalRating = rating,
            notes = existingNotes,
            posterUrl = posterUrl
        )

        lifecycleScope.launch {
            val savedMovie = if (isEditMode) {
                movieViewModel.updateMovieAndWait(movie)
                movie
            } else {
                val insertedMovieId = movieViewModel.insertMovieAndReturnId(movie).toInt()
                movie.copy(movieId = insertedMovieId)
            }

            syncFirestoreReview(savedMovie)
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
            notes = existingNotes,
            posterUrl = existingPosterUrl
        )

        movieViewModel.deleteMovie(movie)
        reviewsViewModel.deleteMovieReview(movieId)
        finish()
    }

    private fun syncFirestoreReview(movie: MovieEntity) {
        val rating = movie.personalRating
        if (rating != null && rating > 0f) {
            reviewsViewModel.upsertMovieReview(
                movieId = movie.movieId,
                movieTitle = movie.title,
                rating = rating.toDouble(),
                comment = movie.notes.orEmpty(),
                dateWatched = currentDate()
            )
        } else if (isEditMode) {
            reviewsViewModel.deleteMovieReview(movie.movieId)
        }
    }

    private fun updateRatingLabel(rating: Float) {
        binding.tvRatingValue.text = if (rating > 0f) {
            "${rating.formatRating()}/5"
        } else {
            "No rating"
        }
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

private fun String.normalizeMovieTitle(): String {
    return trim()
        .replace(Regex("\\s+"), " ")
        .lowercase()
}

private fun Float.formatRating(): String {
    return if (this % 1f == 0f) {
        toInt().toString()
    } else {
        toString()
    }
}

private fun currentDate(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
}
