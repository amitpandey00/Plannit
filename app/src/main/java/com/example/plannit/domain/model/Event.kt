package com.example.plannit.domain.model

import java.time.LocalDateTime

data class Event(
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
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
