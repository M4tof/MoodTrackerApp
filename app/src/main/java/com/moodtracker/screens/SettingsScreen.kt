package com.moodtracker.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.moodtracker.deviceInfo.RunTimeInfo
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    // State variables for each setting
    var morningTime by remember { mutableFloatStateOf(RunTimeInfo.morningReminderTime) }
    var eveningTime by remember { mutableFloatStateOf(RunTimeInfo.eveningReminderTime) }
    var cheerUpText by remember { mutableStateOf(TextFieldValue(RunTimeInfo.cheerUpText)) }
    var timeBarrier by remember { mutableFloatStateOf(RunTimeInfo.timeBarrier) }
    var greetingText by remember { mutableStateOf(TextFieldValue(RunTimeInfo.greetingText)) }
    var isMorningEnabled by remember { mutableStateOf(RunTimeInfo.isMorningReminderEnabled) }
    var isEveningEnabled by remember { mutableStateOf(RunTimeInfo.isEveningReminderEnabled) }

    // Create a coroutine scope for launching the coroutine when saving settings
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as? Activity

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Notification permission denied.", Toast.LENGTH_SHORT).show()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = "Settings", fontSize = 32.sp, fontWeight = FontWeight.Bold)

        // Morning Reminder
        Text(text = "Morning Reminder Time")
        Row (    verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()) {

            TextField(
                value = morningTime.toString(),
                onValueChange = { newTime ->
                    morningTime = newTime.toFloatOrNull() ?: morningTime
                },
                label = { Text("Time (in hours, e.g. 8.30)") },
            )
            Switch(
                checked = isMorningEnabled,
                onCheckedChange = { isMorningEnabled = it }
            )
        }

        // Evening Reminder
        Text(text = "Evening Reminder Time")
        Row (    verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = eveningTime.toString(),
                onValueChange = { newTime ->
                    eveningTime = newTime.toFloatOrNull() ?: eveningTime
                },
                label = { Text("Time") },
            )
            Switch(
                checked = isEveningEnabled,
                onCheckedChange = { isEveningEnabled = it }
            )
        }

        // Time Barrier
        Text(text = "Evening time start")
        TextField(
            value = timeBarrier.toString(),
            onValueChange = { newTime ->
                timeBarrier = newTime.toFloatOrNull() ?: timeBarrier
            },
            label = { Text("Time") },
            modifier = Modifier.fillMaxWidth()
        )

        // Cheer Up Text
        Text(text = "Cheer-Up Text")
        TextField(
            value = cheerUpText,
            onValueChange = { newText ->
                cheerUpText = newText
            },
            label = { Text("Cheer-Up Message") },
            modifier = Modifier.fillMaxWidth()
        )

        // Greeting Text
        Text(text = "Greeting Text")
        TextField(
            value = greetingText,
            onValueChange = { newText ->
                greetingText = newText
            },
            label = { Text("Greeting Text") },
            modifier = Modifier.fillMaxWidth()
        )


        // Save Button
        Button(
            onClick = {
                coroutineScope.launch {
                    val hasPermission = hasNotificationPermission(context)

                    // Request permission if not granted
                    if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        // Don't save settings just yet, wait until the user responds
                        return@launch
                    }

                    // Save settings and update RunTimeInfo
                    RunTimeInfo.updateMorningReminder(context, morningTime)
                    RunTimeInfo.updateEveningReminder(context, eveningTime)
                    RunTimeInfo.updateTimeBarrier(context, timeBarrier)
                    RunTimeInfo.updateCheerUpText(context, cheerUpText.text)
                    RunTimeInfo.updateGreetingText(context, greetingText.text)
                    RunTimeInfo.updateMorningReminderEnabled(context, isMorningEnabled)
                    RunTimeInfo.updateEveningReminderEnabled(context, isEveningEnabled)

                    Toast.makeText(context, "Settings saved!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Save Settings")
        }


    }
}

fun hasNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    } else {
        true // Permissions auto-granted for < Android 13
    }
}


