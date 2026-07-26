package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cut_sessions")
data class CutSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clientName: String,
    val fadeType: String,
    val operatingMode: String,
    val durationSeconds: Long,
    val consistencyScore: Int, // 0 - 100
    val guardsUsedSummary: String,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
