package com.moodtracker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.moodtracker.database.MoodDataRepository
import com.moodtracker.database.MoodDatabaseMain
import com.moodtracker.database.MoodReadingEntry

class DatabaseViewmodel(application: Application) : AndroidViewModel(application) {

    val readAllData: LiveData<List<MoodReadingEntry>>
    val finishedReadings: LiveData<List<MoodReadingEntry>>
    private val repository: MoodDataRepository

    init {
        val dataDao = MoodDatabaseMain.Companion.getDatabase(application).dataDao()
        repository = MoodDataRepository(dataDao)
        readAllData = repository.readAllData

        finishedReadings = readAllData.map { list ->
            list.filter { it.isOver }
        }

    }

    suspend fun getGivenDayReading(date: String): MoodReadingEntry? {
        return repository.getGivenDayReading(date)
    }

    suspend fun addNewReading(data: MoodReadingEntry) {
        repository.addNewReading(data)
    }

    suspend fun updateExistingReading(data: MoodReadingEntry) {
        repository.updateExistingReading(data)
    }

    suspend fun closeOlderEntries(today: String) {
        repository.closeOlderEntries(today)
    }

}