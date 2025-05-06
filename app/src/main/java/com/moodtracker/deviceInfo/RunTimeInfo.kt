package com.moodtracker.deviceInfo

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Calendar

object RunTimeInfo {
    var isTablet: Boolean = false
        private set
    var darkTheme by mutableStateOf(false)
        private set

    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    val hour = c.get(Calendar.HOUR_OF_DAY)
    val minute = c.get(Calendar.MINUTE)

    var morningReminderTime by mutableFloatStateOf(8.30f)
        private set
    var eveningReminderTime by mutableFloatStateOf(8.30f)
        private set
    var timeBarrier by mutableFloatStateOf(12.37f)
        private set
    var cheerUpText by mutableStateOf("DON'T!")
        private set
    var greetingText by mutableStateOf("☕")
        private set

    var isEvening = true // Default, will update continuously

    fun initialize(context: Context) {
        val configuration = context.resources.configuration
        isTablet = (configuration.screenWidthDp > 600) && (configuration.screenHeightDp > 480)

        // Load saved values
        DataStoreManager.getMorningReminder(context).collectInBackground {
            morningReminderTime = it
        }
        DataStoreManager.getEveningReminder(context).collectInBackground {
            eveningReminderTime = it
        }
        DataStoreManager.getCheerUpText(context).collectInBackground {
            cheerUpText = it
        }
        DataStoreManager.getEveningTimeBarrier(context).collectInBackground {
            timeBarrier = it
            isEvening = (hour + (minute/100.0f)) >= timeBarrier
        }
        DataStoreManager.getGreetingText(context).collectInBackground {
            greetingText = it
        }

    }

    suspend fun updateMorningReminder(context: Context, time: Float) {
        morningReminderTime = time
        DataStoreManager.saveMorningReminder(context, time)
    }
    suspend fun updateEveningReminder(context: Context, time: Float) {
        eveningReminderTime = time
        DataStoreManager.saveEveningReminder(context, time)
    }
    suspend fun updateCheerUpText(context: Context, text: String) {
        cheerUpText = text
        DataStoreManager.saveCheerUpText(context, text)
    }
    suspend fun updateTimeBarrier(context: Context, time: Float){
        timeBarrier = time
        DataStoreManager.saveEveningTimeBarrier(context,time)
    }
    suspend fun updateGreetingText(context: Context, text: String) {
        greetingText = text
        DataStoreManager.saveGreetingText(context, text)
    }

    fun flipTheme() {
        darkTheme = !darkTheme
    }
}


fun <T> Flow<T>.collectInBackground(action: (T) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        collect {
            action(it)
        }
    }
}