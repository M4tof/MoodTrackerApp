package com.moodtracker.screens

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

// TODO: if admin/admin then can show all

@Composable
fun SynchroScreen(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var serverAddress by remember { mutableStateOf("") }
    var pingResult by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    var showRegisterDialog by remember { mutableStateOf(false) }
    var registerUsername by remember { mutableStateOf("") }
    var registerPassword by remember { mutableStateOf("") }
    var registerResult by remember { mutableStateOf<String?>(null) }

    var showLoginDialog by remember { mutableStateOf(false) }
    var loginUsername by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginResult by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top-right Server Address button
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Button(onClick = { showDialog = true }) {
                Text("Server address")
            }
        }

        // Centered login/register buttons
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Button(
                onClick = {
                    if (pingResult?.contains("success", true) == true) {
                        showLoginDialog = true
                    } else {
                        loginResult = "Ping the server first!"
                    }
                }
            ) {
                Text("Log In")
            }


            Button(
                onClick = {
                    if (pingResult?.contains("success", true) == true) {
                        showRegisterDialog = true
                    } else {
                        registerResult = "Ping the server first!"
                    }
                }
            ) {
                Text("Register")
            }


            // Show result of the ping
            pingResult?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (it.contains("success", ignoreCase = true)) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
            }

            registerResult?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (it.contains("success", ignoreCase = true)) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
            }

            loginResult?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (it.lowercase().contains("fail") || it.lowercase().contains("error")) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }


        }

        // Dialog to enter server address
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Enter Server IP Address") },
                text = {
                    Column {
                        Text("Example: 192.168.1.119")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = serverAddress,
                            onValueChange = { serverAddress = it },
                            label = { Text("Server IP") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog = false
                            coroutineScope.launch {
                                val result = pingServer(serverAddress)
                                pingResult = result.toString()
                            }
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showRegisterDialog) {
            AlertDialog(
                onDismissRequest = { showRegisterDialog = false },
                title = { Text("Register New Account") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = registerUsername,
                            onValueChange = { registerUsername = it },
                            label = { Text("Username") },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = registerPassword,
                            onValueChange = { registerPassword = it },
                            label = { Text("Password") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val response = registerUser(serverAddress, registerUsername, registerPassword)
                                registerResult = response
                            }
                            showRegisterDialog = false
                        }
                    ) {
                        Text("Register")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRegisterDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showLoginDialog) {
            AlertDialog(
                onDismissRequest = { showLoginDialog = false },
                title = { Text("Log In") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = loginUsername,
                            onValueChange = { loginUsername = it },
                            label = { Text("Username") },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = loginPassword,
                            onValueChange = { loginPassword = it },
                            label = { Text("Password") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val (success, response) = loginUser(serverAddress, loginUsername, loginPassword)
                                loginResult = response

                                if (success) {
                                    val userId = Regex("user\\s*id\\s*[:=]\\s*(\\d+)", RegexOption.IGNORE_CASE)
                                        .find(response)
                                        ?.groupValues?.get(1)
                                        ?.toIntOrNull()

                                    if (userId != null) {
                                        val encodedIp = Uri.encode(serverAddress)
                                        navController.navigate("logged_in/$encodedIp/$userId")
                                    } else {
                                        loginResult = "Login successful, but user ID not found in response: $response"
                                    }
                                }

                            }
                            showLoginDialog = false
                        }
                    ) {
                        Text("Login")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLoginDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

    }
}

// Function to ping the server using IP + port 8080
suspend fun pingServer(ip: String): String {
    return withContext(Dispatchers.IO) {
        val urlString = "http://$ip:8080/"
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 3000
            connection.readTimeout = 3000
            connection.requestMethod = "GET"
            connection.connect()

            if (connection.responseCode == 200) {
                "Ping success: Server responded with 200 OK"
            } else {
                "Ping failed: Server responded with ${connection.responseCode}"
            }
        } catch (e: Exception) {
            "Ping failed: ${e.localizedMessage ?: "Unknown error"}"
        }
    }
}

suspend fun registerUser(serverIp: String, username: String, password: String): String {
    return try {
        val url = "http://$serverIp:8080/users/add"
        val json = """
            {
                "username": "$username",
                "password": "$password"
            }
        """.trimIndent()

        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val response: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(json)
        }

        if (response.status.value in 200..299) {
            "Registration successful!"
        } else {
            "Error: ${response.status.description}"
        }

    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}

suspend fun loginUser(serverIp: String, username: String, password: String): Pair<Boolean, String> {
    return try {
        val client = HttpClient(CIO)

        val response: HttpResponse = client.post("http://$serverIp:8080/users/login") {
            url {
                parameters.append("username", username)
                parameters.append("password", password)
            }
        }

        if (response.status.value == 200) {
            val body = response.bodyAsText()
            Pair(true, body)  // Return success + body (e.g. "UserID:1")
        } else {
            Pair(false, "Login failed: ${response.status.description}")
        }
    } catch (e: Exception) {
        Pair(false, "Login failed: ${e.message}")
    }
}
