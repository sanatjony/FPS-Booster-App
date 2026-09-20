package com.fpsbooster.network

import com.fpsbooster.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object EcosystemService {

    // GitHub foydalanuvchi nomi (o'zingiznikiga almashtirishingiz mumkin)
    var githubUsername: String = "BYSHAHBAJ"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    private suspend fun getJson(url: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val req = Request.Builder().url(url).header("Cache-Control", "no-cache").build()
            client.newCall(req).execute().use { res ->
                if (res.isSuccessful) {
                    Result.success(res.body?.string() ?: "")
                } else {
                    Result.failure(Exception("HTTP xato: ${res.code}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchEvents(): Result<List<EventItem>> = withContext(Dispatchers.IO) {
        val url = "https://raw.githubusercontent.com/$githubUsername/EVENTS/main/events.json"
        getJson(url).mapCatching { json ->
            val type = object : TypeToken<List<EventItem>>() {}.type
            gson.fromJson(json, type)
        }
    }

    suspend fun fetchNotification(): Result<NotificationConfig> = withContext(Dispatchers.IO) {
        val url = "https://raw.githubusercontent.com/$githubUsername/NOTIFICATION/main/notification.json"
        getJson(url).mapCatching { json ->
            gson.fromJson(json, NotificationConfig::class.java)
        }
    }

    suspend fun fetchVideos(): Result<List<VideoItem>> = withContext(Dispatchers.IO) {
        val url = "https://raw.githubusercontent.com/$githubUsername/VIDEOS/main/video.json"
        getJson(url).mapCatching { json ->
            val res = gson.fromJson(json, VideosResponse::class.java)
            res.videos ?: emptyList()
        }
    }

    suspend fun fetchMusic(): Result<List<MusicItem>> = withContext(Dispatchers.IO) {
        val url = "https://raw.githubusercontent.com/$githubUsername/MUSIC/main/music.json"
        getJson(url).mapCatching { json ->
            val type = object : TypeToken<List<MusicItem>>() {}.type
            gson.fromJson(json, type)
        }
    }

    suspend fun fetchImages(): Result<List<ImageItem>> = withContext(Dispatchers.IO) {
        val url = "https://raw.githubusercontent.com/$githubUsername/IMAGES/main/image.json"
        getJson(url).mapCatching { json ->
            val type = object : TypeToken<List<ImageItem>>() {}.type
            gson.fromJson(json, type)
        }
    }

    suspend fun fetchTopContent(): Result<TopContentConfig> = withContext(Dispatchers.IO) {
        val url = "https://raw.githubusercontent.com/$githubUsername/TOPCONTENT/main/topcontent.json"
        getJson(url).mapCatching { json ->
            gson.fromJson(json, TopContentConfig::class.java)
        }
    }

    fun getImageUrl(imageName: String): String {
        return "https://raw.githubusercontent.com/$githubUsername/IMAGES/main/$imageName"
    }
}
