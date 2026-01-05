package com.example.plannit.data.local.dao

import androidx.room.*
import com.example.plannit.data.local.entity.SyncStateEntity

@Dao
interface SyncStateDao {
    @Query("SELECT * FROM sync_state WHERE calendarId = :calendarId")
    suspend fun getSyncState(calendarId: String): SyncStateEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncState(syncState: SyncStateEntity)
    
    @Update
    suspend fun updateSyncState(syncState: SyncStateEntity)
    
    @Query("DELETE FROM sync_state WHERE calendarId = :calendarId")
    suspend fun deleteSyncState(calendarId: String)
    
    @Query("SELECT * FROM sync_state")
    suspend fun getAllSyncStates(): List<SyncStateEntity>
}
