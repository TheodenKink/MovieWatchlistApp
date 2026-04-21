package com.aris.moviewatchlist.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    private const val DAILY_REMINDER_WORK_NAME = "daily_pending_movies_reminder"
    private const val TEST_REMINDER_WORK_NAME = "test_pending_movies_reminder"

    fun scheduleDailyPendingMovieReminder(context: Context) {
        val reminderRequest = PeriodicWorkRequestBuilder<PendingMoviesReminderWorker>(
            1,
            TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(context.applicationContext)
            .enqueueUniquePeriodicWork(
                DAILY_REMINDER_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                reminderRequest
            )
    }

    fun runPendingMovieReminderNow(context: Context) {
        val reminderRequest = OneTimeWorkRequestBuilder<PendingMoviesReminderWorker>().build()

        WorkManager.getInstance(context.applicationContext)
            .enqueueUniqueWork(
                TEST_REMINDER_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                reminderRequest
            )
    }
}
