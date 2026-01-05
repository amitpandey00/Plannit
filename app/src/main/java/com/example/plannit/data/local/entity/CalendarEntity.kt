package com.example.plannit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "calendars")
data class CalendarEntity(
    @PrimaryKey
    val id: String, // Google Calendar ID
    val summary: String,
    val description: String = "",
    val color: Int,
    val isSelected: Boolean = false,
    val isPrimary: Boolean = false,
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
