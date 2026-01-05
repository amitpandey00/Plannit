package com.example.plannit.domain.repository

import com.example.plannit.domain.model.Event
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface EventRepository {
    fun getAllEvents(): Flow<List<Event>>
    fun getEventsBetween(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Event>>
    suspend fun getEventById(id: Long): Event?
    suspend fun insertEvent(event: Event): Long
    suspend fun updateEvent(event: Event)
    suspend fun deleteEvent(event: Event)
    suspend fun deleteEventById(id: Long)
}
