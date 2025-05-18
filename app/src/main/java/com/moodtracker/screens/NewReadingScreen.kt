package com.moodtracker.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moodtracker.R
import com.moodtracker.database.MoodReadingEntry
import com.moodtracker.deviceInfo.RunTimeInfo.day
import com.moodtracker.deviceInfo.RunTimeInfo.isEvening
import com.moodtracker.deviceInfo.RunTimeInfo.month
import com.moodtracker.deviceInfo.RunTimeInfo.year
import com.moodtracker.ui.theme.Pink40
import com.moodtracker.viewmodels.DatabaseViewmodel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NewReadingScreen() {

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val viewModel: DatabaseViewmodel = viewModel()
    val context = LocalContext.current

    var selectedMood by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            scrollState.scrollTo(scrollState.maxValue / 2)

            val date = String.format("%04d-%02d-%02d", year, month + 1, day)
            val existingEntry = viewModel.getGivenDayReading(date)

            selectedMood = if (isEvening) {
                existingEntry?.eveningMood
            } else {
                existingEntry?.morningMood
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        val coroutineScope = rememberCoroutineScope()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Currently choosing mood for:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = if (isEvening) {
                    "${day}.${month + 1}.${year} Evening"
                } else {
                    "${day}.${month + 1}.${year} Morning"
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Choose from one of the faces...")
            Text(text = "←  \uD83D\uDE42  →")

            Spacer(modifier = Modifier.height(12.dp))

            val buttonModifier = Modifier.size(64.dp)
            val transparent = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            val highlighted = ButtonDefaults.buttonColors(containerColor = Pink40) // Light cyan

            fun moodButton(
                faceId: Int,
                drawableId: Int,
                description: String
            ): @Composable () -> Unit = {
                val isSelected = selectedMood == faceId

                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = if (isSelected) 0.7f else 1.0f
                            scaleX = if (isSelected) 1.1f else 1f
                            scaleY = if (isSelected) 1.1f else 1f
                        }
                        .border(
                            width = if (isSelected) 3.dp else 0.dp,
                            color = if (isSelected) Color.White else Color.Transparent,
                            shape = CircleShape
                        )
                ) {
                    Button(
                        onClick = {
                            onClickHandler(faceId, viewModel, context, coroutineScope)
                            selectedMood = faceId
                        },
                        modifier = buttonModifier,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color.White.copy(alpha = 0.15f) else Color.Transparent
                        ),
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Image(
                            painter = painterResource(id = drawableId),
                            contentDescription = description,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    moodButton(-3, R.drawable.angry, "Angry face")()
                    moodButton(-2, R.drawable.sad, "Sad face")()
                }

                moodButton(-1, R.drawable.upset, "Upset face")()
                moodButton(0, R.drawable.neutral, "Neutral face")()
                moodButton(1, R.drawable.pleased, "Pleased face")()

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    moodButton(3, R.drawable.happy, "Happy face")()
                    moodButton(2, R.drawable.excited, "Excited face")()
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* TODO */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("... or click here for a form to help you choose.")
            }
        }
    }
}

// TODO click on button to add entry, link to form and make the form screen

fun onClickHandler(
    faceId: Int,
    viewModel: DatabaseViewmodel,
    context: Context,
    coroutineScope: CoroutineScope
) {
    var date = String.format("%04d-%02d-%02d", year, month + 1, day)
    var modify = false

    coroutineScope.launch {
        val existingEntry = viewModel.getGivenDayReading(date)
        modify = existingEntry != null

        when (Pair(modify, isEvening)) {
            Pair(true, true) -> { // Modify evening reading, so copy what there already was and modify evening
                existingEntry?.let { old ->
                    val modifiedRead = old.copy(
                        eveningMood = faceId,
                    )
                    viewModel.updateExistingReading(modifiedRead)
                }
            }

            Pair(true, false) -> { // modyfing morning reading, copy and change
                existingEntry?.let { old ->
                    val modifiedRead = old.copy(
                        morningMood = faceId,
                    )
                    viewModel.updateExistingReading(modifiedRead)
                }

            }

            Pair(false, true) -> { //new reading for evening, there was no morning reading
                val newReading = MoodReadingEntry(0,date,null,faceId,false)
                viewModel.addNewReading(newReading)
            }

            Pair(false, false) -> { //new reading this day
                val newReading = MoodReadingEntry(0,date,faceId,null,false)
                viewModel.addNewReading(newReading)
            }

        }

    }

}