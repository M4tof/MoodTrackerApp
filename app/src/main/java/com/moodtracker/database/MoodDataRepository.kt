package com.moodtracker.database

import androidx.lifecycle.LiveData

class MoodDataRepository(private val moodDataDao: MoodDataDao) {

    val readAllData: LiveData<List<MoodReadingEntry>> = moodDataDao.readAllData()

    suspend fun addNewReading(data: MoodReadingEntry) {
        moodDataDao.addNewReading(data)
    }

    suspend fun updateExistingReading(data: MoodReadingEntry){
        moodDataDao.updateReading(data)
    }

    suspend fun getDrinkCount(): Int {
        return moodDataDao.getReadingsCount()
    }

    suspend fun deleteAll() {
        return moodDataDao.deleteAll()
    }

}