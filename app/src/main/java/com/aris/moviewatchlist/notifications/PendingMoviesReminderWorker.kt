package com.aris.moviewatchlist.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aris.moviewatchlist.data.local.AppDatabase

class PendingMoviesReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            NotificationHelper.createNotificationChannel(applicationContext)

            val pendingMovieCount = AppDatabase.getDatabase(applicationContext)
                .movieDao()
                .getPendingMovieCount()

            NotificationHelper.showPendingMoviesNotification(
                context = applicationContext,
                pendingMovieCount = pendingMovieCount
            )

            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}
