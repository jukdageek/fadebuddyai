package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "client_profiles")
data class ClientProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val hairTexture: String = "Wavy", // Straight, Wavy, Curly, Coily
    val hairDensity: String = "Medium", // Light, Medium, Dense
    val preferredFadeType: String = "Mid Fade",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
