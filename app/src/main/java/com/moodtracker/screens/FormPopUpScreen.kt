package com.moodtracker.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moodtracker.R

@Composable
fun TemporaryMoodForm() {
    val questions = listOf(
        "Did you sleep well last night?",
        "Are you feeling productive today?",
        "Have you eaten recently?",
        "Do you feel socially connected?",
        "Are you feeling free of stress?"
    )

    val scores = remember { mutableStateMapOf<String, Int>() }
    questions.forEach { question ->
        scores.putIfAbsent(question, 0)
    }

    var extraScore by remember { mutableStateOf(0) }
    var extraAnswered by remember { mutableStateOf(false) }

    val recommendedReading = scores.values.sum()

    // Reset extra question if it is not supposed to be shown
    val showExtraQuestion = recommendedReading > 1 || recommendedReading < -1
    LaunchedEffect(showExtraQuestion) {
        if (!showExtraQuestion) {
            extraScore = 0
            extraAnswered = false
        }
    }

    // Final score logic: use extraScore only if extra question is visible and answered
    val finalScore = if (showExtraQuestion && extraAnswered) {
        extraScore
    } else {
        recommendedReading
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Mood Form", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))

        questions.forEach { question ->
            Text(question, fontWeight = FontWeight.Medium)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                listOf("Yes" to 1, "Neutral" to 0, "No" to -1).forEach { (label, value) ->
                    val isSelected = scores[question] == value
                    Button(
                        onClick = { scores[question] = value },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color.Gray else Color.LightGray
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(label)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (showExtraQuestion) {
            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(12.dp))

            val extraQuestion = if (recommendedReading > 1) {
                "Do you feel more excited than happy?"
            } else {
                "Do you feel more angry than sad?"
            }

            Text(extraQuestion, fontWeight = FontWeight.Medium)

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                val options = if (recommendedReading > 1) {
                    listOf("Excited" to 3, "Happy" to 2)
                } else {
                    listOf("Angry" to -3, "Sad" to -2)
                }

                options.forEach { (label, value) ->
                    val isSelected = extraScore == value && extraAnswered
                    Button(
                        onClick = {
                            extraScore = value
                            extraAnswered = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color.Gray else Color.LightGray
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(label)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(12.dp))

        val moodDrawable = when (finalScore.coerceIn(-3, 3)) {
            -3 -> R.drawable.angry
            -2 -> R.drawable.sad
            -1 -> R.drawable.upset
            0 -> R.drawable.neutral
            1 -> R.drawable.pleased
            2 -> R.drawable.happy
            3 -> R.drawable.excited
            else -> R.drawable.neutral
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Your Mood Suggestion:", fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = painterResource(id = moodDrawable),
                contentDescription = "Mood face",
                modifier = Modifier.size(96.dp),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

