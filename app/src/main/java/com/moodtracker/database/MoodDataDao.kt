package com.moodtracker.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MoodDataDao {
    @Query("Select * FROM mood_data_table order by id ASC")
    fun readAllData(): LiveData<List<MoodReadingEntry>>

    @Query("SELECT * from mood_data_table where id in (:id)")
    fun loadAllByIds(id: IntArray): List<MoodReadingEntry>

    @Query("SELECT COUNT(*) FROM mood_data_table")
    suspend fun getReadingsCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNewReading(reading: MoodReadingEntry)

    @Update
    suspend fun updateReading(reading: MoodReadingEntry)

    @Query("Delete from mood_data_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM mood_data_table WHERE date = :todayDate")
    suspend fun getGivenDayReading(todayDate: String): MoodReadingEntry?

    @Query("UPDATE mood_data_table SET isOver = 1 WHERE date < :todayDate")
    suspend fun markAllOlderThanTodayAsOver(todayDate: String)

}