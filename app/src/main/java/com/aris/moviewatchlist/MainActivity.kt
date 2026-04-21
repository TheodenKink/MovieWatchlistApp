package com.aris.moviewatchlist

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.aris.moviewatchlist.databinding.ActivityMainBinding
import com.aris.moviewatchlist.ui.movies.AddMovieActivity
import com.aris.moviewatchlist.ui.movies.MovieAdapter
import com.aris.moviewatchlist.ui.movies.MovieViewModel
import com.aris.moviewatchlist.ui.watchlists.WatchlistsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val movieViewModel: MovieViewModel by viewModels()
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
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
                }
                startActivity(intent)
            }
        )

        binding.recyclerMovies.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
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
    }

    private fun observeMovies() {
        movieViewModel.allMovies.observe(this) { movies ->
            movieAdapter.submitList(movies)
        }
    }
}
