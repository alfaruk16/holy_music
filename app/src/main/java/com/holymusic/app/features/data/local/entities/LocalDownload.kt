package com.holymusic.app.features.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "download"
)
data class LocalDownload(
    @PrimaryKey val id: String,
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

