package com.moodtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moodtracker.deviceInfo.RunTimeInfo
import com.moodtracker.screens.EntryScreen
import com.moodtracker.screens.NewReadingScreen
import com.moodtracker.screens.SettingsScreen
import com.moodtracker.ui.theme.MoodTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RunTimeInfo.initialize(this)
        enableEdgeToEdge()
        setContent {

            MoodTrackerTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "entry",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("entry") {
                            EntryScreen(
                                onNewReadingClick = { navController.navigate("new_reading") },
                                onStatisticsClick = { /* TODO: navController.navigate("statistics") */ },
                                onOptionsClick = { navController.navigate("settings_screen") },
                                onThemeToggleClick = { /* TODO: RunTimeInfo.flipTheme() */ }
                            )
                        }
                        composable("new_reading") {
                            NewReadingScreen()
                        }
                        composable("settings_screen") {
                            SettingsScreen()
                        }
                    }
                }
            }

        }
    }
}
