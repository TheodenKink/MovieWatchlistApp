package com.aris.moviewatchlist.ui.watchlists

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.aris.moviewatchlist.data.local.entity.WatchlistEntity
import com.aris.moviewatchlist.databinding.ActivityWatchlistsBinding

class WatchlistsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWatchlistsBinding
    private val watchlistViewModel: WatchlistViewModel by viewModels()
    private lateinit var watchlistAdapter: WatchlistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWatchlistsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        observeWatchlists()
    }

    private fun setupRecyclerView() {
        watchlistAdapter = WatchlistAdapter(
            onItemClick = { watchlist ->
                openWatchlistDetails(watchlist)
            },
            onEditClick = { watchlist ->
                openEditWatchlist(watchlist)
            },
            onDeleteClick = { watchlist ->
                showDeleteConfirmation(watchlist)
            }
        )

        binding.recyclerWatchlists.apply {
            layoutManager = LinearLayoutManager(this@WatchlistsActivity)
            adapter = watchlistAdapter
        }
    }

    private fun setupListeners() {
        binding.fabAddWatchlist.setOnClickListener {
            startActivity(Intent(this, AddWatchlistActivity::class.java))
        }
    }

    private fun observeWatchlists() {
        watchlistViewModel.allWatchlists.observe(this) { watchlists ->
            watchlistAdapter.submitList(watchlists)
        }
    }

    private fun openWatchlistDetails(watchlist: WatchlistEntity) {
        val intent = Intent(this, WatchlistDetailsActivity::class.java).apply {
            putExtra("watchlistId", watchlist.watchlistId)
            putExtra("watchlistName", watchlist.name)
        }
        startActivity(intent)
    }

    private fun openEditWatchlist(watchlist: WatchlistEntity) {
        val intent = Intent(this, AddWatchlistActivity::class.java).apply {
            putExtra("watchlistId", watchlist.watchlistId)
            putExtra("name", watchlist.name)
            putExtra("description", watchlist.description)
        }
        startActivity(intent)
    }

    private fun showDeleteConfirmation(watchlist: WatchlistEntity) {
        AlertDialog.Builder(this)
            .setTitle("Delete Watchlist")
            .setMessage("Delete \"${watchlist.name}\"?")
            .setPositiveButton("Delete") { _, _ ->
                watchlistViewModel.deleteWatchlist(watchlist)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
