package com.holymusic.app.features.data.local.model

data class Download(
    val id: String,
    val title: String,
    val artistName: String,
    val description: String,
    val localPath: String,
    val contentBaseUrl: String,
    val imageUrl: String,
    val duration: String,
    val percentage: Int,
    val isComplete: Boolean,
    val contentType: String
)


