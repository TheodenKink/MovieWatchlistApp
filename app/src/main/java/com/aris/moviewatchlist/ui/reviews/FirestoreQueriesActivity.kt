package com.aris.moviewatchlist.ui.reviews

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.aris.moviewatchlist.data.remote.model.Review
import com.aris.moviewatchlist.databinding.ActivityFirestoreQueriesBinding

class FirestoreQueriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFirestoreQueriesBinding
    private lateinit var reviewAdapter: ReviewAdapter
    private val reviewsViewModel: ReviewsViewModel by viewModels()
    private var currentReviewSource: LiveData<List<Review>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFirestoreQueriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        showEmptyState("Choose a Firestore query to display review results")
    }

    private fun setupRecyclerView() {
        reviewAdapter = ReviewAdapter(
            onItemClick = { },
            onDeleteClick = { },
            showDeleteButton = false
        )

        binding.recyclerReviewQueryResults.apply {
            layoutManager = LinearLayoutManager(this@FirestoreQueriesActivity)
            adapter = reviewAdapter
        }
    }

    private fun setupListeners() {
        binding.btnHighRatingReviews.setOnClickListener {
            observeReviewResults(
                source = reviewsViewModel.getHighRatingReviews(),
                emptyMessage = "No high rating reviews found"
            )
        }

        binding.btnSearchReviewsByMovie.setOnClickListener {
            val movieTitle = binding.editMovieTitle.text?.toString()?.trim().orEmpty()
            if (movieTitle.isBlank()) {
                Toast.makeText(this, "Enter a movie title first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            observeReviewResults(
                source = reviewsViewModel.getReviewsForMovie(movieTitle),
                emptyMessage = "No reviews for $movieTitle"
            )
        }

        binding.btnSortReviewsByDate.setOnClickListener {
            observeReviewResults(
                source = reviewsViewModel.getReviewsOrderedByDate(),
                emptyMessage = "No reviews with a watched date found"
            )
        }
    }

    private fun observeReviewResults(
        source: LiveData<List<Review>>,
        emptyMessage: String
    ) {
        currentReviewSource?.removeObservers(this)
        currentReviewSource = source
        source.observe(this) { reviews ->
            showResults(reviews, emptyMessage)
        }
    }

    private fun showResults(reviews: List<Review>, emptyMessage: String) {
        reviewAdapter.submitList(reviews)
        if (reviews.isEmpty()) {
            showEmptyState(emptyMessage)
        } else {
            binding.recyclerReviewQueryResults.visibility = View.VISIBLE
            binding.tvEmptyState.visibility = View.GONE
        }
    }

    private fun showEmptyState(message: String) {
        reviewAdapter.submitList(emptyList())
        binding.tvEmptyState.text = message
        binding.tvEmptyState.visibility = View.VISIBLE
        binding.recyclerReviewQueryResults.visibility = View.INVISIBLE
    }
}
