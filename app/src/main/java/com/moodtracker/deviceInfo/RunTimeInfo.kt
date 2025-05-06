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
            // Recalculate isEvening when the barrier is loaded
            updateIsEvening() // First check when the barrier is loaded
        }
        DataStoreManager.getGreetingText(context).collectInBackground {
            greetingText = it
        }

        // Start checking isEvening continuously
        startUpdatingIsEvening()
    }

    private fun startUpdatingIsEvening() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                updateIsEvening()
                delay(60000) // Wait for 1 minute before checking again
//                TODO: na minutach nie działa
            }
        }
    }

    private fun updateIsEvening() {
        val now = Calendar.getInstance()
        val preciseTime = now.get(Calendar.HOUR_OF_DAY) + (now.get(Calendar.MINUTE) / 60f)
        isEvening = preciseTime >= timeBarrier
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
        updateIsEvening() // Update immediately after barrier change
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