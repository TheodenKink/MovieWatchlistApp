package com.aris.moviewatchlist.ui.reviews

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.aris.moviewatchlist.data.remote.model.Review
import com.aris.moviewatchlist.databinding.ActivityReviewsBinding

class ReviewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewsBinding
    private lateinit var reviewAdapter: ReviewAdapter
    private val reviewsViewModel: ReviewsViewModel by viewModels()

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
            onDeleteClick = { review -> showDeleteConfirmation(review) }
        )

        binding.recyclerReviews.apply {
            layoutManager = LinearLayoutManager(this@ReviewsActivity)
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
            reviewAdapter.submitList(reviews)
            binding.tvEmptyState.visibility =
                if (reviews.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerReviews.visibility =
                if (reviews.isEmpty()) View.INVISIBLE else View.VISIBLE
        }

        reviewsViewModel.operationMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
