package com.moodtracker.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

//TODO: enter addres of the server, probe for it.

@Composable
fun SynchroScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = { showLoginPopup() }) {
                Text("Log In")
            }
            Button(onClick = { showRegisterPopup() }) {
                Text("Register")
            }
        }
    }
}

// Placeholder functions for future popups
fun showLoginPopup() {
    // TODO: Implement login popup
}

fun showRegisterPopup() {
    // TODO: Implement register popup
}
