package com.aris.moviewatchlist.ui.reviews

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.aris.moviewatchlist.R
import com.aris.moviewatchlist.data.local.entity.MovieEntity
import com.aris.moviewatchlist.data.remote.model.Review
import com.aris.moviewatchlist.databinding.ActivityReviewsBinding
import com.aris.moviewatchlist.ui.movies.MovieViewModel

class ReviewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewsBinding
    private lateinit var reviewAdapter: ReviewAdapter
    private val reviewsViewModel: ReviewsViewModel by viewModels()
    private val movieViewModel: MovieViewModel by viewModels()
    private var latestReviews: List<Review> = emptyList()
    private var latestMovies: List<MovieEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReviewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        observeReviews()
    }

    private fun setupRecyclerView() {
        reviewAdapter = ReviewAdapter(
            onItemClick = { review -> openEditReview(review) },
            onDeleteClick = { review -> showDeleteConfirmation(review) },
            showDeleteButton = false
        )

        binding.recyclerReviews.apply {
            layoutManager = GridLayoutManager(
                this@ReviewsActivity,
                resources.getInteger(R.integer.movie_grid_span_count)
            )
            adapter = reviewAdapter
        }
    }

    private fun setupListeners() {
        binding.fabAddReview.visibility = View.GONE
        binding.fabAddReview.setOnClickListener {
            startActivity(Intent(this, AddEditReviewActivity::class.java))
        }
    }

    private fun observeReviews() {
        reviewsViewModel.reviews.observe(this) { reviews ->
            latestReviews = reviews
            showReviewsWithMoviePosters()
            binding.tvEmptyState.visibility =
                if (reviews.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerReviews.visibility =
                if (reviews.isEmpty()) View.INVISIBLE else View.VISIBLE
        }

        movieViewModel.allMovies.observe(this) { movies ->
            latestMovies = movies
            showReviewsWithMoviePosters()
        }

        reviewsViewModel.operationMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showReviewsWithMoviePosters() {
        val reviewsWithPosters = latestReviews.map { review ->
            if (!review.posterUrl.isNullOrBlank()) {
                review
            } else {
                review.copy(posterUrl = findMatchingMovie(review)?.posterUrl)
            }
        }

        reviewAdapter.submitList(reviewsWithPosters)
    }

    private fun findMatchingMovie(review: Review): MovieEntity? {
        if (review.movieId > 0) {
            latestMovies.firstOrNull { movie -> movie.movieId == review.movieId }?.let { return it }
        }

        val reviewTitle = review.movieTitle.normalizeTitleForMatch()
        return latestMovies.firstOrNull { movie ->
            movie.title.normalizeTitleForMatch() == reviewTitle
        }
    }

    private fun openEditReview(review: Review) {
        val intent = Intent(this, AddEditReviewActivity::class.java).apply {
            putExtra("reviewId", review.reviewId)
            putExtra("movieTitle", review.movieTitle)
            putExtra("movieId", review.movieId)
            putExtra("username", review.username)
            putExtra("rating", review.rating)
            putExtra("comment", review.comment)
            putExtra("dateWatched", review.dateWatched)
            putExtra("posterUrl", review.posterUrl)
        }
        startActivity(intent)
    }

    private fun showDeleteConfirmation(review: Review) {
        AlertDialog.Builder(this)
            .setTitle("Delete Review")
            .setMessage("Delete review for \"${review.movieTitle}\"?")
            .setPositiveButton("Delete") { _, _ ->
                reviewsViewModel.deleteReview(review.reviewId) {
                    // Snapshot listener refreshes the list.
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

private fun String.normalizeTitleForMatch(): String {
    return trim()
        .replace(Regex("\\s+"), " ")
        .lowercase()
}
