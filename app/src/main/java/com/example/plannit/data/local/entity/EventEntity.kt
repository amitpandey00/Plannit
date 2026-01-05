package com.example.plannit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val location: String = "",
    val color: Int = 0xFF2196F3.toInt(),
    val isAllDay: Boolean = false,
    val notificationMinutesBefore: Int = 15,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    
    // Google Calendar sync fields
    val googleEventId: String? = null,
    val googleCalendarId: String? = null,
    val etag: String? = null,
    val recurringRule: String? = null, // RRULE string
    val recurringEventId: String? = null, // Parent recurring event ID
    val startTimezone: String? = null,
    val endTimezone: String? = null,
    val isDeleted: Boolean = false,
    val isDirty: Boolean = false, // Needs to be pushed to Google
    val lastSyncedAt: LocalDateTime? = null
)
