package com.moodtracker.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TextField
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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

    // Create a coroutine scope for launching the coroutine when saving settings
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

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
        TextField(
            value = morningTime.toString(),
            onValueChange = { newTime ->
                morningTime = newTime.toFloatOrNull() ?: morningTime
            },
            label = { Text("Time (in hours, e.g. 8.30)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Evening Reminder
        Text(text = "Evening Reminder Time")
        TextField(
            value = eveningTime.toString(),
            onValueChange = { newTime ->
                eveningTime = newTime.toFloatOrNull() ?: eveningTime
            },
            label = { Text("Time") },
            modifier = Modifier.fillMaxWidth()
        )

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
                // Launch the coroutine from the correct scope
                coroutineScope.launch {
                    // Save settings and update RunTimeInfo
                    RunTimeInfo.updateMorningReminder(context, morningTime)
                    RunTimeInfo.updateEveningReminder(context, eveningTime)
                    RunTimeInfo.updateTimeBarrier(context,timeBarrier)
                    RunTimeInfo.updateCheerUpText(context, cheerUpText.text)
                    RunTimeInfo.updateGreetingText(context, greetingText.text)

                    // Show confirmation toast
                    Toast.makeText(context, "Settings saved!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Save Settings")
        }

    }
}

