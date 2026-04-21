package com.aris.moviewatchlist.ui.reviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aris.moviewatchlist.data.remote.model.Review
import com.aris.moviewatchlist.databinding.ItemReviewBinding

class ReviewAdapter(
    private val onItemClick: (Review) -> Unit,
    private val onDeleteClick: (Review) -> Unit,
    private val showDeleteButton: Boolean = true
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    private var reviews: List<Review> = emptyList()

    inner class ReviewViewHolder(
        private val binding: ItemReviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review) {
            binding.tvMovieTitle.text = review.movieTitle
            binding.tvReviewMeta.text = "${review.username} - ${review.rating.formatRating()}/5 - ${review.dateWatched}"
            binding.tvComment.text = review.comment.ifBlank { "No comment" }

            binding.root.setOnClickListener {
                onItemClick(review)
            }

            binding.btnDeleteReview.visibility = if (showDeleteButton) View.VISIBLE else View.GONE
            binding.btnDeleteReview.setOnClickListener {
                onDeleteClick(review)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount(): Int = reviews.size

    fun submitList(updatedReviews: List<Review>) {
        reviews = updatedReviews
        notifyDataSetChanged()
    }
}

private fun Double.formatRating(): String {
    return if (this % 1.0 == 0.0) {
        toInt().toString()
    } else {
        toString()
    }
}
