package com.aris.moviewatchlist.ui.watchlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aris.moviewatchlist.data.local.entity.MovieEntity
import com.aris.moviewatchlist.databinding.ItemWatchlistMovieBinding

class WatchlistMovieAdapter(
    private val onItemClick: (MovieEntity) -> Unit
) : RecyclerView.Adapter<WatchlistMovieAdapter.WatchlistMovieViewHolder>() {

    private var movieList: List<MovieEntity> = emptyList()

    inner class WatchlistMovieViewHolder(
        private val binding: ItemWatchlistMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieEntity) {
            binding.tvTitle.text = movie.title
            binding.tvMeta.text = "${movie.genre} • ${movie.year} • ${movie.platform}"
            binding.tvStatus.text = if (movie.isWatched) "✓ Watched" else "◌ Pending"

            binding.root.setOnClickListener {
                onItemClick(movie)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchlistMovieViewHolder {
        val binding = ItemWatchlistMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WatchlistMovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WatchlistMovieViewHolder, position: Int) {
        holder.bind(movieList[position])
    }

    override fun getItemCount(): Int = movieList.size

    fun submitList(movies: List<MovieEntity>) {
        movieList = movies
        notifyDataSetChanged()
    }
}