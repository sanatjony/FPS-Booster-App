package com.fpsbooster.model

import com.google.gson.annotations.SerializedName

// EVENTS/events.json
data class EventItem(
    @SerializedName("title") val title: String,
    @SerializedName("type") val type: String?,
    @SerializedName("update_date") val updateDate: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("region") val region: String?,
    @SerializedName("link") val link: String?
)

// NOTIFICATION/notification.json
data class NotificationConfig(
    @SerializedName("enabled") val enabled: Boolean,
    @SerializedName("title") val title: String,
    @SerializedName("message") val message: String
)

// VIDEOS/video.json
data class VideosResponse(
    @SerializedName("video") val videos: List<VideoItem>
)

data class VideoItem(
    @SerializedName("title") val title: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("youtubeUrl") val youtubeUrl: String
)

// MUSIC/music.json
data class MusicItem(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String,
    @SerializedName("version") val version: String?
)

// IMAGES/image.json
data class ImageItem(
    @SerializedName("image") val image: String,
    @SerializedName("title") val title: String
)

// TOPCONTENT/topcontent.json
data class TopContentConfig(
    @SerializedName("home_top") val homeTop: String
)
