package com.moodtracker.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moodtracker.database.MoodReadingEntry
import com.moodtracker.viewmodels.DatabaseViewmodel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Composable
fun LoggedInScreen(
    serverIp: String,
    userId: Int,
    viewModel: DatabaseViewmodel
) {
    var moodData by remember { mutableStateOf<List<MoodEntry>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    var isSyncing by remember { mutableStateOf(false) }
    var syncProgress by remember { mutableStateOf(0) }
    var syncTotal by remember { mutableStateOf(0) }

    val finishedEntries by viewModel.finishedReadings.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        val result = fetchRemoteMoodData(serverIp, userId)
        when (result) {
            is Result.Success -> {
                moodData = result.data
                errorMessage = null
            }
            is Result.Failure -> {
                errorMessage = result.message
                moodData = emptyList()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("You are logged in!", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        // Synchronize Button
        Button(onClick = {
            coroutineScope.launch {
                isSyncing = true
                syncProgress = 0
                syncTotal = 0

                val result = synchronizeMoodData(serverIp, userId, finishedEntries){ current, total ->
                    syncProgress = current
                    syncTotal = total
                }

                isSyncing = false

                if (result is Result.Failure) {
                    errorMessage = result.message
                } else {
                    val refreshed = fetchRemoteMoodData(serverIp, userId)
                    if (refreshed is Result.Success) {
                        moodData = refreshed.data
                        errorMessage = null
                    } else if (refreshed is Result.Failure) {
                        errorMessage = "Fetch after sync failed: ${refreshed.message}"
                    }
                }
            }
        }) {
            Text("Synchronize")
        }

        if (isSyncing && syncTotal > 0) {
            LinearProgressIndicator(
                progress = syncProgress / syncTotal.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
        }

        LazyColumn {
            items(moodData) { entry ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Date: ${entry.date}")
                        Text(text = "Morning Mood: ${entry.morningMood}")
                        Text(text = "Evening Mood: ${entry.eveningMood}")
                    }
                }
            }
        }
    }
}


sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Failure(val message: String) : Result<Nothing>()
}

@Serializable
data class MoodEntry(
    val id: Int,
    val date: String,
    val morningMood: Int,
    val eveningMood: Int,
)

@Serializable
data class MoodUploadDTO(
    val date: String,
    val morningMood: Int,
    val eveningMood: Int,
    val isOver: Boolean
)

suspend fun fetchRemoteMoodData(serverIp: String, userId: Int): Result<List<MoodEntry>> {
    return try {
        val url = "http://$serverIp:8080/mood/all/$userId"
        val client = HttpClient(CIO)
        val response: HttpResponse = client.get(url)
        val bodyText = response.bodyAsText()

        if (response.status.value in 200..299) {
            val jsonArray = Json.parseToJsonElement(bodyText).jsonArray
            val moodList = jsonArray.mapNotNull { element ->
                try {
                    val obj = element.jsonObject
                    MoodEntry(
                        id = obj["id"]?.jsonPrimitive?.int ?: return@mapNotNull null,
                        date = obj["date"]?.jsonPrimitive?.content ?: return@mapNotNull null,
                        morningMood = obj["morningMood"]?.jsonPrimitive?.int ?: return@mapNotNull null,
                        eveningMood = obj["eveningMood"]?.jsonPrimitive?.int ?: return@mapNotNull null
                    )
                } catch (e: Exception) {
                    null
                }
            }
            Result.Success(moodList)
        } else {
            Result.Failure("Error: ${response.status.description}")
        }
    } catch (e: Exception) {
        Result.Failure("Exception: ${e.message}")
    }
}

suspend fun fetchLastSyncedDate(serverIp: String, userId: Int): Result<String?> {
    return try {
        val client = HttpClient(CIO)
        val url = "http://$serverIp:8080/mood/last-date/$userId"

        val response: HttpResponse = client.get(url)
        val body = response.bodyAsText()

        if (response.status.value in 200..299) {
            if (body == "none") Result.Success(null)
            else Result.Success(body)
        } else {
            Result.Failure("Error: ${response.status.description}")
        }
    } catch (e: Exception) {
        Result.Failure("Exception: ${e.message}")
    }
}

suspend fun synchronizeMoodData(
    serverIp: String,
    userId: Int,
    finishedEntries: List<MoodReadingEntry>,
    onProgress: ((current: Int, total: Int) -> Unit)? = null
): Result<Unit> {
    val lastSyncedResult = fetchLastSyncedDate(serverIp, userId)

    return when (lastSyncedResult) {
        is Result.Failure -> Result.Failure(lastSyncedResult.message)
        is Result.Success -> {
            val lastDate = lastSyncedResult.data
            val toSend = if (lastDate == null) {
                finishedEntries
            } else {
                finishedEntries.filter { it.date > lastDate }
            }

            println("🔄 Sync Debug: ${toSend.size} entries will be sent to the server.")

            val client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(Json { encodeDefaults = true })
                }
            }

            toSend.forEachIndexed { index, entry ->
                onProgress?.invoke(index + 1, toSend.size)

                val dto = MoodUploadDTO(
                    date = entry.date,
                    morningMood = entry.morningMood!!,
                    eveningMood = entry.eveningMood!!,
                    isOver = entry.isOver
                )

                try {
                    val response = client.post("http://$serverIp:8080/mood/add/$userId") {
                        contentType(ContentType.Application.Json)
                        setBody(dto)
                    }

                    if (response.status.value !in 200..299) {
                        println("❌ Failed to upload entry for ${entry.date}: ${response.status}")
                    } else {
                        println("✅ Uploaded entry for ${entry.date}")
                    }
                } catch (e: Exception) {
                    println("🚫 Exception while uploading ${entry.date}: ${e.message}")
                }
            }

            // Final sync-complete call
            try {
                val finalResponse = client.post("http://$serverIp:8080/mood/sync-complete/$userId")
                if (finalResponse.status.value in 200..299) {
                    println("✅ Sync completed successfully.")
                    return Result.Success(Unit)
                } else {
                    println("⚠️ Sync-complete failed: ${finalResponse.status}")
                    return Result.Failure("Sync-complete failed: ${finalResponse.status}")
                }
            } catch (e: Exception) {
                return Result.Failure("Exception during sync-complete: ${e.message}")
            }
        }
    }
}


