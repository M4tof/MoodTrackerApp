package com.moodtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moodtracker.screens.EntryScreen
import com.moodtracker.screens.NewReadingScreen
import com.moodtracker.ui.theme.MoodTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                                onNewReadingClick = { navController.navigate("new_reading") }
                            )
                        }
                        composable("new_reading") {
                            NewReadingScreen()
                        }
                    }
                }
            }

        }
    }
}
