package com.example.plannit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "sync_state")
data class SyncStateEntity(
    @PrimaryKey
    val calendarId: String,
    val syncToken: String?,
    val lastSyncAt: LocalDateTime?,
    val lastSyncStatus: String = "success", // success, error, in_progress
    val errorMessage: String? = null
)
