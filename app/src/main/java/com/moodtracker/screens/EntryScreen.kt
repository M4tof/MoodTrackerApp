package com.moodtracker.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moodtracker.deviceInfo.RunTimeInfo.greetingText
import com.moodtracker.deviceInfo.RunTimeInfo.hour
import com.moodtracker.deviceInfo.RunTimeInfo.minute

@Composable
fun EntryScreen(
    modifier: Modifier = Modifier,
    onNewReadingClick: () -> Unit = {},
    onStatisticsClick: () -> Unit = {},
    onOptionsClick: () -> Unit = {},
    onThemeToggleClick: () -> Unit = {},
    onSynchronize: () -> Unit = {}
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Center

        ) {

            val realTime = hour + (minute/100.0f)
            val time = realTime.toString()
            Text(
                text = time,
                fontSize = 16.sp,
            )

            Text(
                text = "Welcome to the MoodTracker",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = greetingText,
                fontSize = 12.sp,
            )

            Spacer(modifier = Modifier.padding(12.dp))

            Button(onClick = onNewReadingClick, modifier = Modifier.fillMaxWidth()) {
                Text("New reading")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onStatisticsClick, modifier = Modifier.fillMaxWidth()) {
                Text("Statistics")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onSynchronize, modifier = Modifier.fillMaxWidth()) {
                Text("Synchronise with foreign device")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onOptionsClick, modifier = Modifier.fillMaxWidth()) {
                Text("Options")
            }
        }
    }
}


