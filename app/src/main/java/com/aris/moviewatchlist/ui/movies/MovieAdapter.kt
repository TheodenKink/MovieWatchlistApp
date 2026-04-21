package com.aris.moviewatchlist.ui.movies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.aris.moviewatchlist.data.local.entity.MovieEntity
import com.aris.moviewatchlist.databinding.ItemMovieBinding

class MovieAdapter(
    private val onItemClick: (MovieEntity) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private var movieList: List<MovieEntity> = emptyList()

    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieEntity) {
            binding.tvTitle.text = movie.title
            binding.tvMeta.text = "${movie.genre} • ${movie.year} • ${movie.platform}"
            binding.tvStatus.text = if (movie.isWatched) "✓ Watched" else "◌ Pending"

            binding.root.setOnClickListener {
                onItemClick(movie)
            }

            binding.imgPoster.load(movie.posterUrl) {
                placeholder(android.R.drawable.ic_menu_report_image)
                error(android.R.drawable.ic_menu_report_image)
                fallback(android.R.drawable.ic_menu_report_image)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movieList[position])
    }

    override fun getItemCount(): Int = movieList.size

    fun submitList(movies: List<MovieEntity>) {
        movieList = movies
        notifyDataSetChanged()
    }
}
