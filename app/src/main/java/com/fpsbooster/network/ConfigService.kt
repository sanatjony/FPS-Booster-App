package com.fpsbooster.network

import com.fpsbooster.model.ServerResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

object ConfigService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    /**
     * GitHub Raw URL orqali config.json ni o'qiydi
     */
    suspend fun fetchConfig(rawConfigUrl: String): Result<ServerResponse> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(rawConfigUrl)
                .header("Cache-Control", "no-cache")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("HTTP xatolik: ${response.code}"))
                }
                val body = response.body?.string() ?: return@withContext Result.failure(Exception("Bo'sh javob keldi"))
                val config = gson.fromJson(body, ServerResponse::class.java)
                Result.success(config)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Patch faylini yuklab olib, ilovaning vaqtinchalik keshiga saqlaydi
     */
    suspend fun downloadPatchFile(downloadUrl: String, destinationFile: File): Result<File> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(downloadUrl).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("Yuklab olishda xatolik: ${response.code}"))
                }

                response.body?.byteStream()?.use { input ->
                    FileOutputStream(destinationFile).use { output ->
                        input.copyTo(output)
                    }
                }
                Result.success(destinationFile)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
