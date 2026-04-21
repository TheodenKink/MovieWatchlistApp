package com.aris.moviewatchlist.ui.watchlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aris.moviewatchlist.data.local.entity.WatchlistEntity
import com.aris.moviewatchlist.databinding.ItemWatchlistBinding

class WatchlistAdapter(
    private val onItemClick: (WatchlistEntity) -> Unit,
    private val onEditClick: (WatchlistEntity) -> Unit,
    private val onDeleteClick: (WatchlistEntity) -> Unit
) : RecyclerView.Adapter<WatchlistAdapter.WatchlistViewHolder>() {

    private var watchlistList: List<WatchlistEntity> = emptyList()

    inner class WatchlistViewHolder(private val binding: ItemWatchlistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(watchlist: WatchlistEntity) {
            binding.tvWatchlistName.text = watchlist.name

            binding.tvWatchlistDescription.text =
                if (watchlist.description.isNullOrBlank()) {
                    "No description"
                } else {
                    watchlist.description
                }

            binding.tvMovieCount.text = "Open watchlist"

            binding.root.setOnClickListener {
                onItemClick(watchlist)
            }

            binding.btnEditWatchlist.setOnClickListener {
                onEditClick(watchlist)
            }

            binding.btnDeleteWatchlist.setOnClickListener {
                onDeleteClick(watchlist)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchlistViewHolder {
        val binding = ItemWatchlistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WatchlistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WatchlistViewHolder, position: Int) {
        holder.bind(watchlistList[position])
    }

    override fun getItemCount(): Int = watchlistList.size

    fun submitList(watchlists: List<WatchlistEntity>) {
        watchlistList = watchlists
        notifyDataSetChanged()
    }
}
