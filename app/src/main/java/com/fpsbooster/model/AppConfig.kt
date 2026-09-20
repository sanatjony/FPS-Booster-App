package com.fpsbooster.model

import com.google.gson.annotations.SerializedName

data class ServerResponse(
    @SerializedName("app_version") val appVersion: String,
    @SerializedName("configurations") val configurations: Map<String, GameConfig>
)

data class GameConfig(
    @SerializedName("name") val name: String,
    @SerializedName("package_name") val packageName: String,
    @SerializedName("target_dir") val targetDir: String,
    @SerializedName("file_name") val fileName: String,
    @SerializedName("download_url") val downloadUrl: String,
    @SerializedName("description") val description: String
)
