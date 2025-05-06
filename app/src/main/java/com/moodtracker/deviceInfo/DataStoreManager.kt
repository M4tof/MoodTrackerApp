package com.moodtracker.deviceInfo

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "user_prefs"
private val Context.dataStore by preferencesDataStore(DATASTORE_NAME)

object DataStoreManager {
    private val MORNING_REMINDER_KEY = floatPreferencesKey("morning_reminder_time")
    private val EVENING_REMINDER_KEY = floatPreferencesKey("evening_reminder_time")
    private val CHEER_UP_TEXT_KEY = stringPreferencesKey("cheer_up_text")
    private val EVENING_TIME_BARRIER = floatPreferencesKey("evening_time_barrier")
    private val GREETING_TEXT = stringPreferencesKey("greeting_text")

    suspend fun saveMorningReminder(context: Context, time: Float) {
        context.dataStore.edit { prefs ->
            prefs[MORNING_REMINDER_KEY] = time
        }
    }
    suspend fun saveEveningReminder(context: Context, time: Float) {
        context.dataStore.edit { prefs ->
            prefs[EVENING_REMINDER_KEY] = time
        }
    }
    suspend fun saveCheerUpText(context: Context, text: String) {
        context.dataStore.edit { prefs ->
            prefs[CHEER_UP_TEXT_KEY] = text
        }
    }
    suspend fun saveEveningTimeBarrier(context: Context, time: Float) {
        context.dataStore.edit { prefs ->
            prefs[EVENING_TIME_BARRIER] = time
        }
    }
    suspend fun saveGreetingText(context: Context, text: String) {
        context.dataStore.edit { prefs ->
            prefs[GREETING_TEXT] = text
        }
    }

    fun getMorningReminder(context: Context): Flow<Float> {
        return context.dataStore.data.map { prefs ->
            prefs[MORNING_REMINDER_KEY] ?: 8.30f
        }
    }
    fun getEveningReminder(context: Context): Flow<Float> {
        return context.dataStore.data.map { prefs ->
            prefs[EVENING_REMINDER_KEY] ?: 21.30f
        }
    }
    fun getCheerUpText(context: Context): Flow<String> {
        return context.dataStore.data.map { prefs ->
            prefs[CHEER_UP_TEXT_KEY] ?: "https://youtu.be/VtvGZyYV0EE?si=ZXsJvHx8AnSGim6R"
        }
    }
    fun getEveningTimeBarrier(context: Context): Flow<Float> {
        return context.dataStore.data.map { prefs ->
            prefs[EVENING_TIME_BARRIER] ?: 12.37f
        }
    }
    fun getGreetingText(context: Context): Flow<String> {
        return context.dataStore.data.map { prefs ->
            prefs[GREETING_TEXT] ?: "Smacznej kawusi ☕"
        }
    }




}
