package com.example.plannit.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.plannit.data.local.converter.Converters
import com.example.plannit.data.local.dao.CalendarDao
import com.example.plannit.data.local.dao.EventDao
import com.example.plannit.data.local.dao.SyncStateDao
import com.example.plannit.data.local.dao.TaskDao
import com.example.plannit.data.local.entity.CalendarEntity
import com.example.plannit.data.local.entity.EventEntity
import com.example.plannit.data.local.entity.SyncStateEntity
import com.example.plannit.data.local.entity.TaskEntity

@Database(
    entities = [
        EventEntity::class,
        TaskEntity::class,
        CalendarEntity::class,
        SyncStateEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PlannitDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun taskDao(): TaskDao
    abstract fun calendarDao(): CalendarDao
    abstract fun syncStateDao(): SyncStateDao

    companion object {
        @Volatile
        private var INSTANCE: PlannitDatabase? = null

        fun getDatabase(context: Context): PlannitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlannitDatabase::class.java,
                    "plannit_database"
                )
                    .fallbackToDestructiveMigration() // For development; use proper migrations in production
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
