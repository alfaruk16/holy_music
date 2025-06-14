package com.holymusic.app.features.data.local.model

data class FileItem(
    val id: String,
    val url: String,
    val name: String,
    val mimeType: String,
    val artistName: String,
    val description: String,
    val contentBaseUrl: String,
    val imageUrl: String,
    val duration: String
)