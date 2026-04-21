package com.aris.moviewatchlist.ui.queries

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.aris.moviewatchlist.data.local.entity.MovieEntity
import com.aris.moviewatchlist.data.local.entity.WatchlistEntity
import com.aris.moviewatchlist.data.local.relations.WatchlistWithMovies
import com.aris.moviewatchlist.databinding.ActivityQueriesBinding
import com.aris.moviewatchlist.ui.movies.AddMovieActivity
import com.aris.moviewatchlist.ui.movies.MovieViewModel
import com.aris.moviewatchlist.ui.reviews.FirestoreQueriesActivity
import com.aris.moviewatchlist.ui.watchlists.WatchlistViewModel

class QueriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQueriesBinding
    private lateinit var resultsAdapter: QueryResultsAdapter
    private val movieViewModel: MovieViewModel by viewModels()
    private val watchlistViewModel: WatchlistViewModel by viewModels()

    private var currentMovieSource: LiveData<List<MovieEntity>>? = null
    private var currentWatchlistSource: LiveData<WatchlistWithMovies>? = null
    private var watchlists: List<WatchlistEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQueriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupWatchlistSpinner()
        setupListeners()
        showEmptyState("Choose a query to display movie results")
    }

    private fun setupRecyclerView() {
        resultsAdapter = QueryResultsAdapter { movie ->
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

        binding.recyclerQueryResults.apply {
            layoutManager = LinearLayoutManager(this@QueriesActivity)
            adapter = resultsAdapter
        }
    }

    private fun setupWatchlistSpinner() {
        watchlistViewModel.allWatchlists.observe(this) { updatedWatchlists ->
            watchlists = updatedWatchlists
            val names = if (updatedWatchlists.isEmpty()) {
                listOf("No watchlists available")
            } else {
                updatedWatchlists.map { it.name }
            }

            binding.spinnerWatchlists.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                names
            )
        }
    }

    private fun setupListeners() {
        binding.btnWatchedMovies.setOnClickListener {
            observeMovieResults(
                source = movieViewModel.getWatchedMovies(),
                emptyMessage = "No watched movies found"
            )
        }

        binding.btnSearchGenre.setOnClickListener {
            val genre = binding.editGenre.text?.toString()?.trim().orEmpty()
            if (genre.isBlank()) {
                Toast.makeText(this, "Enter a genre first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            observeMovieResults(
                source = movieViewModel.getMoviesByGenre(genre),
                emptyMessage = "No movies found for \"$genre\""
            )
        }

        binding.btnShowWatchlistMovies.setOnClickListener {
            if (watchlists.isEmpty()) {
                showEmptyState("Create a watchlist first")
                return@setOnClickListener
            }

            val selectedPosition = binding.spinnerWatchlists.selectedItemPosition
            val selectedWatchlist = watchlists.getOrNull(selectedPosition) ?: return@setOnClickListener
            observeWatchlistResults(
                source = watchlistViewModel.getWatchlistWithMovies(selectedWatchlist.watchlistId),
                emptyMessage = "No movies in ${selectedWatchlist.name}"
            )
        }

        binding.btnFirestoreQueries.setOnClickListener {
            startActivity(Intent(this, FirestoreQueriesActivity::class.java))
        }
    }

    private fun observeMovieResults(
        source: LiveData<List<MovieEntity>>,
        emptyMessage: String
    ) {
        clearActiveQueryObservers()
        currentMovieSource = source
        source.observe(this) { movies ->
            showResults(movies, emptyMessage)
        }
    }

    private fun observeWatchlistResults(
        source: LiveData<WatchlistWithMovies>,
        emptyMessage: String
    ) {
        clearActiveQueryObservers()
        currentWatchlistSource = source
        source.observe(this) { watchlistWithMovies ->
            showResults(watchlistWithMovies.movies, emptyMessage)
        }
    }

    private fun clearActiveQueryObservers() {
        currentMovieSource?.removeObservers(this)
        currentWatchlistSource?.removeObservers(this)
        currentMovieSource = null
        currentWatchlistSource = null
    }

    private fun showResults(movies: List<MovieEntity>, emptyMessage: String) {
        resultsAdapter.submitList(movies)
        if (movies.isEmpty()) {
            showEmptyState(emptyMessage)
        } else {
            binding.recyclerQueryResults.visibility = View.VISIBLE
            binding.tvEmptyState.visibility = View.GONE
        }
    }

    private fun showEmptyState(message: String) {
        resultsAdapter.submitList(emptyList())
        binding.tvEmptyState.text = message
        binding.tvEmptyState.visibility = View.VISIBLE
        binding.recyclerQueryResults.visibility = View.INVISIBLE
    }
}
