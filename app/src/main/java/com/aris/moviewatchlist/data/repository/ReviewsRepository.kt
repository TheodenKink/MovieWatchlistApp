package com.aris.moviewatchlist.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aris.moviewatchlist.data.remote.model.Review
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class ReviewsRepository(
    private val firestore: FirebaseFirestore? = try {
        FirebaseFirestore.getInstance()
    } catch (_: IllegalStateException) {
        null
    }
) {

    private var reviewsListener: ListenerRegistration? = null

    fun observeReviews(): LiveData<List<Review>> {
        val reviewsLiveData = MutableLiveData<List<Review>>()
        val reviewsCollection = firestore?.collection("reviews")
        if (reviewsCollection == null) {
            reviewsLiveData.value = emptyList()
            return reviewsLiveData
        }

        reviewsListener?.remove()
        reviewsListener = reviewsCollection
            .orderBy("movieTitle", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    reviewsLiveData.value = emptyList()
                    return@addSnapshotListener
                }

                val reviews = snapshot?.documents.orEmpty().map { document ->
                    document.toObject(Review::class.java)?.copy(reviewId = document.id)
                        ?: Review(reviewId = document.id)
                }
                reviewsLiveData.value = reviews
            }

        return reviewsLiveData
    }

    fun getHighRatingReviews(): LiveData<List<Review>> {
        val reviewsCollection = firestore?.collection("reviews")
        return observeReviewQuery(
            query = reviewsCollection
                ?.whereGreaterThanOrEqualTo("rating", 8.0)
                ?.orderBy("rating", Query.Direction.DESCENDING)
        )
    }

    fun getReviewsForMovie(movieTitle: String): LiveData<List<Review>> {
        val reviewsCollection = firestore?.collection("reviews")
        return observeReviewQuery(
            query = reviewsCollection?.whereEqualTo("movieTitle", movieTitle)
        )
    }

    fun getReviewsOrderedByDate(): LiveData<List<Review>> {
        val reviewsCollection = firestore?.collection("reviews")
        return observeReviewQuery(
            query = reviewsCollection?.orderBy("dateWatched", Query.Direction.DESCENDING)
        )
    }

    private fun observeReviewQuery(query: Query?): LiveData<List<Review>> {
        val reviewsLiveData = MutableLiveData<List<Review>>()
        if (query == null) {
            reviewsLiveData.value = emptyList()
            return reviewsLiveData
        }

        reviewsListener?.remove()
        reviewsListener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                reviewsLiveData.value = emptyList()
                return@addSnapshotListener
            }

            val reviews = snapshot?.documents.orEmpty().map { document ->
                document.toObject(Review::class.java)?.copy(reviewId = document.id)
                    ?: Review(reviewId = document.id)
            }
            reviewsLiveData.value = reviews
        }

        return reviewsLiveData
    }

    fun createReview(
        review: Review,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val reviewsCollection = firestore?.collection("reviews")
        if (reviewsCollection == null) {
            onComplete(false, FIREBASE_NOT_CONFIGURED)
            return
        }

        val document = reviewsCollection.document()
        document
            .set(review.copy(reviewId = document.id))
            .addOnSuccessListener { onComplete(true, null) }
            .addOnFailureListener { onComplete(false, it.message) }
    }

    fun updateReview(
        review: Review,
        onComplete: (Boolean, String?) -> Unit
    ) {
        if (review.reviewId.isBlank()) {
            onComplete(false, "Missing review id")
            return
        }

        val reviewsCollection = firestore?.collection("reviews")
        if (reviewsCollection == null) {
            onComplete(false, FIREBASE_NOT_CONFIGURED)
            return
        }

        reviewsCollection.document(review.reviewId)
            .set(review)
            .addOnSuccessListener { onComplete(true, null) }
            .addOnFailureListener { onComplete(false, it.message) }
    }

    fun deleteReview(
        reviewId: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        if (reviewId.isBlank()) {
            onComplete(false, "Missing review id")
            return
        }

        val reviewsCollection = firestore?.collection("reviews")
        if (reviewsCollection == null) {
            onComplete(false, FIREBASE_NOT_CONFIGURED)
            return
        }

        reviewsCollection.document(reviewId)
            .delete()
            .addOnSuccessListener { onComplete(true, null) }
            .addOnFailureListener { onComplete(false, it.message) }
    }

    fun clear() {
        reviewsListener?.remove()
        reviewsListener = null
    }

    companion object {
        private const val FIREBASE_NOT_CONFIGURED =
            "Firebase is not configured. Add app/google-services.json first."
    }
}
