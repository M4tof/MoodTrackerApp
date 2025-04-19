package com.moodtracker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.moodtracker.database.MoodDataRepository
import com.moodtracker.database.MoodDatabaseMain
import com.moodtracker.database.MoodReadingEntry

class DatabaseViewmodel(application: Application) : AndroidViewModel(application) {
    val readAllData: LiveData<List<MoodReadingEntry>>
    private val repository: MoodDataRepository

    init {
        val dataDao = MoodDatabaseMain.Companion.getDatabase(application).dataDao()
        repository = MoodDataRepository(dataDao)
        readAllData = repository.readAllData
    }

}