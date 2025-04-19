package com.moodtracker.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_data_table")
data class MoodReadingEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val morningMood: Int,
    val eveningMood: Int,
    val isOver: Boolean,
    val altIcon: Boolean
    )
