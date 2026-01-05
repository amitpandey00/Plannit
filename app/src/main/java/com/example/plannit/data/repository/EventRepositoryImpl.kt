package com.example.plannit.data.repository

import com.example.plannit.data.local.dao.EventDao
import com.example.plannit.data.mapper.toDomain
import com.example.plannit.data.mapper.toEntity
import com.example.plannit.domain.model.Event
import com.example.plannit.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class EventRepositoryImpl(
    private val eventDao: EventDao
) : EventRepository {

    override fun getAllEvents(): Flow<List<Event>> {
        return eventDao.getAllEvents().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getEventsBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<Event>> {
        return eventDao.getEventsBetween(startDate, endDate).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getEventById(id: Long): Event? {
        return eventDao.getEventById(id)?.toDomain()
    }

    override suspend fun insertEvent(event: Event): Long {
        return eventDao.insertEvent(event.toEntity())
    }

    override suspend fun updateEvent(event: Event) {
        eventDao.updateEvent(event.toEntity())
    }

    override suspend fun deleteEvent(event: Event) {
        eventDao.deleteEvent(event.toEntity())
    }

    override suspend fun deleteEventById(id: Long) {
        eventDao.deleteEventById(id)
    }
}
