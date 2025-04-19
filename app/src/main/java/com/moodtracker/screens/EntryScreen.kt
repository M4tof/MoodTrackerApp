package com.moodtracker.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EntryScreen(
    modifier: Modifier = Modifier,
    onNewReadingClick: () -> Unit = {}
) {
    Box(modifier = modifier.fillMaxSize()) {
        Button(
            onClick = { /* TODO */ },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .height(36.dp)
        ) {
            Text(text = "Theme", fontSize = 12.sp)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to the MoodTracker",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Smacznej Kawusi ; )",
                fontSize = 12.sp,
            )

            Spacer(modifier = Modifier.padding(12.dp))

            Button(onClick = onNewReadingClick, modifier = Modifier.fillMaxWidth()) {
                Text("New reading")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Statistics")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Options")
            }
        }
    }
}

