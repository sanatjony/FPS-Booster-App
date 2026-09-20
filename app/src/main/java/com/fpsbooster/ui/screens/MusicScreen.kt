package com.fpsbooster.ui.screens

import android.media.AudioAttributes
import android.media.MediaPlayer
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
import com.fpsbooster.model.MusicItem
import com.fpsbooster.network.EcosystemService

@Composable
fun MusicScreen() {
    var musicList by remember { mutableStateOf<List<MusicItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var currentPlayingUrl by remember { mutableStateOf<String?>(null) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    LaunchedEffect(Unit) {
        isLoading = true
        EcosystemService.fetchMusic().onSuccess { musicList = it }
        isLoading = false
    }

    fun playAudio(url: String) {
        try {
            if (currentPlayingUrl == url) {
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        it.pause()
                    } else {
                        it.start()
                    }
                }
                return
            }

            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener {
                    it.start()
                    currentPlayingUrl = url
                }
                setOnCompletionListener {
                    currentPlayingUrl = null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "🎵 O'yin Musiqalari Pleyeri",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Rasmiy va nostalgik PUBG saundtreklari",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(musicList) { music ->
            val isThisPlaying = currentPlayingUrl == music.url && (mediaPlayer?.isPlaying == true)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = music.name.replace(".mp3", ""),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(text = "Online Stream", fontSize = 12.sp, color = Color.Gray)
                    }

                    Button(
                        onClick = { playAudio(music.url) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isThisPlaying) Color(0xFFFF5252) else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(text = if (isThisPlaying) "⏸️ To'xtatish" else "▶️ Tinglash")
                    }
                }
            }
        }
    }
}
