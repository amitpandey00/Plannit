package com.example.plannit.data.local.dao

import androidx.room.*
import com.example.plannit.data.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY startDateTime ASC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE startDateTime >= :startDate AND startDateTime < :endDate ORDER BY startDateTime ASC")
    fun getEventsBetween(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: Long): EventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity): Long

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteEventById(id: Long)
    
    // Google Calendar sync queries
    @Query("SELECT * FROM events WHERE googleEventId = :googleEventId AND googleCalendarId = :calendarId")
    suspend fun getEventByGoogleId(googleEventId: String, calendarId: String): EventEntity?
    
    @Query("SELECT * FROM events WHERE isDirty = 1 AND isDeleted = 0")
    suspend fun getDirtyEvents(): List<EventEntity>
    
    @Query("SELECT * FROM events WHERE googleCalendarId = :calendarId AND isDeleted = 0")
    fun getEventsByCalendar(calendarId: String): Flow<List<EventEntity>>
    
    @Query("UPDATE events SET isDeleted = 1, isDirty = 1 WHERE googleEventId = :googleEventId AND googleCalendarId = :calendarId")
    suspend fun markEventAsDeleted(googleEventId: String, calendarId: String)
    
    @Query("DELETE FROM events WHERE isDeleted = 1 AND lastSyncedAt IS NOT NULL")
    suspend fun deleteMarkedEvents()
}
