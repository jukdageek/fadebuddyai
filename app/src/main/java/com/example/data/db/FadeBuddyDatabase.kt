package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ClientProfileEntity::class, CutSessionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FadeBuddyDatabase : RoomDatabase() {
    abstract fun fadeBuddyDao(): FadeBuddyDao

    companion object {
        @Volatile
        private var INSTANCE: FadeBuddyDatabase? = null

        fun getDatabase(context: Context): FadeBuddyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FadeBuddyDatabase::class.java,
                    "fadebuddy_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
