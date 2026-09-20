package com.fpsbooster.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fpsbooster.model.EventItem
import com.fpsbooster.model.NotificationConfig
import com.fpsbooster.network.EcosystemService

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    var notification by remember { mutableStateOf<NotificationConfig?>(null) }
    var events by remember { mutableStateOf<List<EventItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        EcosystemService.fetchNotification().onSuccess { notification = it }
        EcosystemService.fetchEvents().onSuccess { events = it }
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Notification Alert Banner
        notification?.let { notif ->
            if (notif.enabled) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E1A47))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "📢 " + notif.title, fontWeight = FontWeight.Bold, color = Color(0xFFFFD54F), fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = notif.message, color = Color.White, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "🔥 Yangiliklar va Eventlar",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        items(events) { event ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        event.link?.let { url ->
                            if (url.startsWith("http")) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }
                        }
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = event.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        event.region?.let { reg ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = reg,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    event.updateDate?.let { date ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Sana: $date", fontSize = 12.sp, color = Color.Gray)
                    }

                    event.status?.let { status ->
                        Text(text = "Holat: $status", fontSize = 12.sp, color = Color(0xFF81C784))
                    }
                }
            }
        }
    }
}
