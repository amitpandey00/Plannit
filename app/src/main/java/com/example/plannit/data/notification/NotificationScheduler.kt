package com.example.plannit.data.notification

import android.content.Context
import androidx.work.*
import com.example.plannit.domain.model.Event
import com.example.plannit.domain.model.Task
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    fun scheduleEventNotification(event: Event) {
        val notificationTime = event.startDateTime.minusMinutes(event.notificationMinutesBefore.toLong())
        val delay = Duration.between(LocalDateTime.now(), notificationTime)

        if (delay.isNegative) return

        val data = workDataOf(
            "title" to "Event Reminder",
            "message" to event.title,
            "notificationId" to event.id.toInt()
        )

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "event_${event.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun scheduleTaskNotification(task: Task) {
        val dueDateTime = task.dueDateTime ?: return
        val minutesBefore = task.notificationMinutesBefore ?: return
        val notificationTime = dueDateTime.minusMinutes(minutesBefore.toLong())
        val delay = Duration.between(LocalDateTime.now(), notificationTime)

        if (delay.isNegative) return

        val data = workDataOf(
            "title" to "Task Reminder",
            "message" to task.title,
            "notificationId" to task.id.toInt()
        )

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "task_${task.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelEventNotification(eventId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork("event_$eventId")
    }

    fun cancelTaskNotification(taskId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork("task_$taskId")
    }
}
