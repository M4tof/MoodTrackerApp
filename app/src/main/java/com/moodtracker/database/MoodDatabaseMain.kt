package com.moodtracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MoodReadingEntry::class], version = 2, exportSchema = false)
abstract class MoodDatabaseMain: RoomDatabase(){

    abstract fun dataDao(): MoodDataDao

    companion object {
        @Volatile
        private var INSTANCE: MoodDatabaseMain? = null

        fun getDatabase(context: Context): MoodDatabaseMain {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MoodDatabaseMain::class.java,
                    "mood_data_table"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}