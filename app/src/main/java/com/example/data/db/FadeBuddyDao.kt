package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FadeBuddyDao {
    @Query("SELECT * FROM client_profiles ORDER BY name ASC")
    fun getAllClientProfiles(): Flow<List<ClientProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClientProfile(profile: ClientProfileEntity): Long

    @Query("DELETE FROM client_profiles WHERE id = :id")
    suspend fun deleteClientProfile(id: Long)

    @Query("SELECT * FROM cut_sessions ORDER BY timestamp DESC")
    fun getAllCutSessions(): Flow<List<CutSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCutSession(session: CutSessionEntity): Long

    @Query("DELETE FROM cut_sessions WHERE id = :id")
    suspend fun deleteCutSession(id: Long)
}
