package com.example.plannit.data.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val title = inputData.getString("title") ?: return Result.failure()
        val message = inputData.getString("message") ?: return Result.failure()
        val notificationId = inputData.getInt("notificationId", 0)

        NotificationHelper.showNotification(
            applicationContext,
            notificationId,
            title,
            message
        )

        return Result.success()
    }
}
