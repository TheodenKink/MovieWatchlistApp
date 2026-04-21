package com.aris.moviewatchlist.ui.watchlists

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.aris.moviewatchlist.data.local.entity.WatchlistEntity
import com.aris.moviewatchlist.databinding.ActivityAddWatchlistBinding

class AddWatchlistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddWatchlistBinding
    private val viewModel: WatchlistViewModel by viewModels()

    private var watchlistId: Int = 0
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddWatchlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        readIntentData()

        binding.btnSave.setOnClickListener {
            saveWatchlist()
        }

        binding.btnDelete.setOnClickListener {
            showDeleteDialog()
        }
    }

    private fun readIntentData() {
        if (intent.hasExtra("watchlistId")) {
            isEditMode = true

            watchlistId = intent.getIntExtra("watchlistId", 0)
            val name = intent.getStringExtra("name").orEmpty()
            val description = intent.getStringExtra("description").orEmpty()

            binding.tvFormTitle.text = "Edit Watchlist"
            binding.btnSave.text = "Update Watchlist"
            binding.btnDelete.visibility = View.VISIBLE

            binding.etName.setText(name)
            binding.etDescription.setText(description)
        }
    }

    private fun saveWatchlist() {
        val name = binding.etName.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()

        if (name.isNotEmpty()) {
            val watchlist = WatchlistEntity(
                watchlistId = if (isEditMode) watchlistId else 0,
                name = name,
                description = description
            )

            if (isEditMode) {
                viewModel.updateWatchlist(watchlist)
            } else {
                viewModel.insertWatchlist(watchlist)
            }

            finish()
        }
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Watchlist")
            .setMessage("Are you sure?")
            .setPositiveButton("Delete") { _, _ ->
                deleteWatchlist()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteWatchlist() {
        val watchlist = WatchlistEntity(
            watchlistId = watchlistId,
            name = binding.etName.text.toString(),
            description = binding.etDescription.text.toString()
        )

        viewModel.deleteWatchlist(watchlist)
        finish()
    }
}