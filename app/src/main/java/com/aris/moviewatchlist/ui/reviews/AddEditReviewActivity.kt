package com.aris.moviewatchlist.ui.reviews

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.aris.moviewatchlist.data.remote.model.Review
import com.aris.moviewatchlist.databinding.ActivityAddEditReviewBinding

class AddEditReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditReviewBinding
    private val reviewsViewModel: ReviewsViewModel by viewModels()

    private var reviewId: String = ""
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddEditReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        readIntentData()
        setupListeners()
        observeMessages()
    }

    private fun readIntentData() {
        if (intent.hasExtra("reviewId")) {
            isEditMode = true
            reviewId = intent.getStringExtra("reviewId").orEmpty()

            binding.tvFormTitle.text = "Edit Review"
            binding.btnSaveReview.text = "Update Review"
            binding.btnDeleteReview.visibility = View.VISIBLE

            binding.etMovieTitle.setText(intent.getStringExtra("movieTitle").orEmpty())
            binding.etUsername.setText(intent.getStringExtra("username").orEmpty())
            binding.etRating.setText(intent.getDoubleExtra("rating", 0.0).toString())
            binding.etComment.setText(intent.getStringExtra("comment").orEmpty())
            binding.etDateWatched.setText(intent.getStringExtra("dateWatched").orEmpty())
        } else {
            binding.tvFormTitle.text = "Add Review"
            binding.btnSaveReview.text = "Save Review"
            binding.btnDeleteReview.visibility = View.GONE
        }
    }

    private fun setupListeners() {
        binding.btnSaveReview.setOnClickListener {
            saveReview()
        }

        binding.btnDeleteReview.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    private fun observeMessages() {
        reviewsViewModel.operationMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveReview() {
        val movieTitle = binding.etMovieTitle.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val rating = binding.etRating.text.toString().toDoubleOrNull()
        val comment = binding.etComment.text.toString().trim()
        val dateWatched = binding.etDateWatched.text.toString().trim()

        if (movieTitle.isBlank() || username.isBlank() || rating == null) {
            Toast.makeText(this, "Movie title, username, and rating are required", Toast.LENGTH_SHORT).show()
            return
        }

        val review = Review(
            reviewId = reviewId,
            movieTitle = movieTitle,
            username = username,
            rating = rating,
            comment = comment,
            dateWatched = dateWatched
        )

        if (isEditMode) {
            reviewsViewModel.updateReview(review) {
                finish()
            }
        } else {
            reviewsViewModel.createReview(review) {
                finish()
            }
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Review")
            .setMessage("Are you sure you want to delete this review?")
            .setPositiveButton("Delete") { _, _ ->
                reviewsViewModel.deleteReview(reviewId) {
                    finish()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
