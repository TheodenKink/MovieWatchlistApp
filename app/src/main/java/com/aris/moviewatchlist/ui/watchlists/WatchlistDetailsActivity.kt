package com.aris.moviewatchlist.ui.watchlists

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.aris.moviewatchlist.R
import com.aris.moviewatchlist.databinding.ActivityWatchlistDetailsBinding
import com.aris.moviewatchlist.ui.movies.AddMovieActivity
import com.aris.moviewatchlist.ui.movies.MovieAdapter

class WatchlistDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWatchlistDetailsBinding
    private val watchlistViewModel: WatchlistViewModel by viewModels()
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWatchlistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val watchlistId = intent.getIntExtra("watchlistId", -1)
        val watchlistName = intent.getStringExtra("watchlistName").orEmpty()

        binding.tvWatchlistTitle.text = watchlistName

        setupRecyclerView()

        if (watchlistId != -1) {
            watchlistViewModel.getWatchlistWithMovies(watchlistId).observe(this) { watchlistWithMovies ->
                val movies = watchlistWithMovies.movies

                binding.tvWatchlistSubtitle.text =
                    if (movies.isEmpty()) "No movies yet"
                    else "${movies.size} movie${if (movies.size == 1) "" else "s"}"

                binding.tvEmptyState.visibility =
                    if (movies.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE

                binding.recyclerWatchlistMovies.visibility =
                    if (movies.isEmpty()) android.view.View.GONE else android.view.View.VISIBLE

                movieAdapter.submitList(movies)
            }
        }
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter { movie ->
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

        binding.recyclerWatchlistMovies.apply {
            layoutManager = GridLayoutManager(
                this@WatchlistDetailsActivity,
                resources.getInteger(R.integer.movie_grid_span_count)
            )
            adapter = movieAdapter
        }
    }
}
