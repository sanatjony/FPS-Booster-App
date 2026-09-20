package com.fpsbooster.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.fpsbooster.injector.PatchManager
import com.fpsbooster.shizuku.ShizukuHelper
import com.fpsbooster.ui.screens.*

enum class AppTab(val title: String, val icon: String) {
    HOME("Home", "🏠"),
    BOOSTER("Booster", "⚡"),
    VIDEOS("Videos", "📺"),
    GALLERY("Gallery", "🖼️"),
    MUSIC("Music", "🎵")
}

class MainActivity : ComponentActivity() {

    private val configUrl = "https://raw.githubusercontent.com/BYSHAHBAJ/FILES/main/fps.json"
    private lateinit var patchManager: PatchManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        patchManager = PatchManager(this)

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFF7C4DFF),
                    background = Color(0xFF101014),
                    surface = Color(0xFF1E1E24)
                )
            ) {
                var selectedTab by remember { mutableStateOf(AppTab.HOME) }

                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = Color.White
                        ) {
                            AppTab.values().forEach { tab ->
                                NavigationBarItem(
                                    selected = selectedTab == tab,
                                    onClick = { selectedTab = tab },
                                    icon = { Text(tab.icon) },
                                    label = { Text(tab.title) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (selectedTab) {
                            AppTab.HOME -> HomeScreen()
                            AppTab.BOOSTER -> BoosterScreen(
                                configUrl = configUrl,
                                patchManager = patchManager,
                                onRequestShizuku = {
                                    ShizukuHelper.requestPermission { granted ->
                                        runOnUiThread {
                                            Toast.makeText(
                                                this@MainActivity,
                                                if (granted) "Shizuku ruxsati berildi!" else "Ruxsat berilmadi!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            )
                            AppTab.VIDEOS -> VideosScreen()
                            AppTab.GALLERY -> GalleryScreen()
                            AppTab.MUSIC -> MusicScreen()
                        }
                    }
                }
            }
        }
    }
}
