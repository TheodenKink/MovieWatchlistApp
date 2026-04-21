package com.aris.moviewatchlist.ui.reviews

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aris.moviewatchlist.data.remote.model.Review
import com.aris.moviewatchlist.data.repository.ReviewsRepository

class ReviewsViewModel : ViewModel() {

    private val repository = ReviewsRepository()
    val reviews: LiveData<List<Review>> = repository.observeReviews()

    private val _operationMessage = MutableLiveData<String>()
    val operationMessage: LiveData<String> = _operationMessage

    fun createReview(review: Review, onSuccess: () -> Unit) {
        repository.createReview(review) { success, errorMessage ->
            handleOperationResult(success, errorMessage, "Review saved", onSuccess)
        }
    }

    fun updateReview(review: Review, onSuccess: () -> Unit) {
        repository.updateReview(review) { success, errorMessage ->
            handleOperationResult(success, errorMessage, "Review updated", onSuccess)
        }
    }

    fun deleteReview(reviewId: String, onSuccess: () -> Unit) {
        repository.deleteReview(reviewId) { success, errorMessage ->
            handleOperationResult(success, errorMessage, "Review deleted", onSuccess)
        }
    }

    fun upsertMovieReview(
        movieId: Int,
        movieTitle: String,
        rating: Double,
        comment: String,
        dateWatched: String,
        onSuccess: () -> Unit = {}
    ) {
        repository.upsertMovieReview(
            movieId = movieId,
            movieTitle = movieTitle,
            rating = rating,
            comment = comment,
            dateWatched = dateWatched
        ) { success, errorMessage ->
            handleOperationResult(success, errorMessage, "Review saved", onSuccess)
        }
    }

    fun deleteMovieReview(movieId: Int, onSuccess: () -> Unit = {}) {
        repository.deleteMovieReview(movieId) { success, errorMessage ->
            handleOperationResult(success, errorMessage, "Review deleted", onSuccess)
        }
    }

    fun getHighRatingReviews(): LiveData<List<Review>> {
        return repository.getHighRatingReviews()
    }

    fun getReviewsForMovie(movieTitle: String): LiveData<List<Review>> {
        return repository.getReviewsForMovie(movieTitle)
    }

    fun getReviewsOrderedByDate(): LiveData<List<Review>> {
        return repository.getReviewsOrderedByDate()
    }

    private fun handleOperationResult(
        success: Boolean,
        errorMessage: String?,
        successMessage: String,
        onSuccess: () -> Unit
    ) {
        if (success) {
            _operationMessage.value = successMessage
            onSuccess()
        } else {
            _operationMessage.value = errorMessage ?: "Firestore operation failed"
        }
    }

    override fun onCleared() {
        repository.clear()
        super.onCleared()
    }
}
