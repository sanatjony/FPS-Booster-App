package com.fpsbooster.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fpsbooster.injector.PatchManager
import com.fpsbooster.model.GameConfig
import com.fpsbooster.model.ServerResponse
import com.fpsbooster.network.ConfigService
import com.fpsbooster.shizuku.ShizukuHelper
import kotlinx.coroutines.launch

@Composable
fun BoosterScreen(
    configUrl: String,
    patchManager: PatchManager,
    onRequestShizuku: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isShizukuReady by remember { mutableStateOf(ShizukuHelper.hasPermission()) }
    var configData by remember { mutableStateOf<ServerResponse?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("Tayyor") }

    LaunchedEffect(Unit) {
        isShizukuReady = ShizukuHelper.hasPermission()
        isLoading = true
        statusText = "Konfiguratsiyalar yuklanmoqda..."
        val result = ConfigService.fetchConfig(configUrl)
        isLoading = false
        if (result.isSuccess) {
            configData = result.getOrNull()
            statusText = "Tayyor"
        } else {
            statusText = "Xatolik: ${result.exceptionOrNull()?.message}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "⚡ 90/120 FPS Booster",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Shizuku yordamida o'yin sozlamalarini optimallashtirish",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Shizuku Holati Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Shizuku Xizmati", fontWeight = FontWeight.SemiBold, color = Color.White)
                    Text(
                        text = if (isShizukuReady) "✅ Faol va ulangan" else "❌ Ulangan emas",
                        fontSize = 12.sp,
                        color = if (isShizukuReady) Color.Green else Color.Red
                    )
                }
                if (!isShizukuReady) {
                    Button(onClick = {
                        onRequestShizuku()
                        isShizukuReady = ShizukuHelper.hasPermission()
                    }) {
                        Text(text = "Ruxsat berish")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(text = statusText, fontSize = 12.sp, color = Color.LightGray)

        Spacer(modifier = Modifier.height(16.dp))

        // O'yinlar ro'yxati
        configData?.configurations?.values?.let { configs ->
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(configs.toList()) { game ->
                    GameCard(
                        game = game,
                        onInstall = {
                            coroutineScope.launch {
                                isLoading = true
                                val res = patchManager.installPatch(game) { progress ->
                                    statusText = progress
                                }
                                isLoading = false
                                statusText = if (res.isSuccess) res.getOrNull() ?: "" else "Xato: ${res.exceptionOrNull()?.message}"
                            }
                        },
                        onRemove = {
                            coroutineScope.launch {
                                isLoading = true
                                val res = patchManager.removePatch(game)
                                isLoading = false
                                statusText = if (res.isSuccess) res.getOrNull() ?: "" else "Xato: ${res.exceptionOrNull()?.message}"
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GameCard(
    game: GameConfig,
    onInstall: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = game.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = game.description, fontSize = 13.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onInstall,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                ) {
                    Text(text = "O'rnatish")
                }

                OutlinedButton(
                    onClick = onRemove,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Tiklash", color = Color.White)
                }
            }
        }
    }
}
