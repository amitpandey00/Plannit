package com.example.plannit.data.local.dao

import androidx.room.*
import com.example.plannit.data.local.entity.CalendarEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarDao {
    @Query("SELECT * FROM calendars ORDER BY isPrimary DESC, summary ASC")
    fun getAllCalendars(): Flow<List<CalendarEntity>>
    
    @Query("SELECT * FROM calendars WHERE isSelected = 1")
    fun getSelectedCalendars(): Flow<List<CalendarEntity>>
    
    @Query("SELECT * FROM calendars WHERE id = :calendarId")
    suspend fun getCalendarById(calendarId: String): CalendarEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalendar(calendar: CalendarEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalendars(calendars: List<CalendarEntity>)
    
    @Update
    suspend fun updateCalendar(calendar: CalendarEntity)
    
    @Query("UPDATE calendars SET isSelected = :isSelected WHERE id = :calendarId")
    suspend fun updateCalendarSelection(calendarId: String, isSelected: Boolean)
    
    @Delete
    suspend fun deleteCalendar(calendar: CalendarEntity)
    
    @Query("DELETE FROM calendars")
    suspend fun deleteAllCalendars()
}
