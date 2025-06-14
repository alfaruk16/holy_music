package com.holymusic.app.features.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "track"
)
data class LocalTrack(
    val about: String? = null,
    val appLanguage: String? = null,
    val artistId: String? = null,
    val artistName: String? = null,
    val category: String? = null,
    val categoryName: String? = null,
    val contentBaseUrl: String? = null,
    val contentCategory: String? = null,
    val createdBy: String? = null,
    val createdOn: String? = null,
    val duration: String? = null,
    val genreId: String? = null,
    @PrimaryKey val id: String,
    val imageUrl: String? = null,
    val isActive: Boolean? = null,
    val lyrics: String? = null,
    val sequenceNo: Int? = null,
    val streamUrl: String? = null,
    val subcategory: String? = null,
    val subcategoryName: String? = null,
    val title: String? = null,
    val updatedBy: String? = null,
    val updatedOn: String? = null,
    val playCount: Int? = null,
    val totalFav: Int? = null,
    val isPremium: Boolean? = null
)
