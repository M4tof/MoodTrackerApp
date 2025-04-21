package com.moodtracker.screens

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moodtracker.R
import com.moodtracker.database.MoodReadingEntry
import com.moodtracker.deviceInfo.RunTimeInfo.timeBarrier
import kotlinx.coroutines.launch
import java.util.Calendar

val c = Calendar.getInstance()
val year = c.get(Calendar.YEAR)
val month = c.get(Calendar.MONTH)
val day = c.get(Calendar.DAY_OF_MONTH)
val hour = c.get(Calendar.HOUR_OF_DAY)
val isEvening = hour >= timeBarrier

@Composable
fun NewReadingScreen() {

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            scrollState.scrollTo(scrollState.maxValue / 2)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
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
                    Button(
                        onClick = { onClickHandler(-3) },
                        modifier = buttonModifier,
                        colors = transparent,
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.angry),
                            contentDescription = "Angry face",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Button(
                        onClick = { onClickHandler(-2) },
                        modifier = buttonModifier,
                        colors = transparent,
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.sad),
                            contentDescription = "Sad face",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Button(
                    onClick = { onClickHandler(-1) },
                    modifier = buttonModifier,
                    colors = transparent,
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.upset),
                        contentDescription = "Upset face",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Button(
                    onClick = { onClickHandler(0) },
                    modifier = buttonModifier,
                    colors = transparent,
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.neutral),
                        contentDescription = "Neutral face",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Button(
                    onClick = { onClickHandler(1) },
                    modifier = buttonModifier,
                    colors = transparent,
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pleased),
                        contentDescription = "Pleased face",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { onClickHandler(2) },
                        modifier = buttonModifier,
                        colors = transparent,
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.happy),
                            contentDescription = "Happy face",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Button(
                        onClick = { onClickHandler(3) },
                        modifier = buttonModifier,
                        colors = transparent,
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.excited),
                            contentDescription = "Excited face",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
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

fun onClickHandler(faceId: Int){
    var date = "${day}.${month + 1}.${year}"
    var isAlt: Boolean;
    if (faceId == -3 || faceId == 3){
        isAlt = true
    }
    else{
        isAlt = false
    }


    val newData: MoodReadingEntry = MoodReadingEntry(0,date,faceId,null,false,isAlt)



}