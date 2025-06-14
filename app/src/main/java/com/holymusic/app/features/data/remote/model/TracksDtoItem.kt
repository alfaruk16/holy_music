package com.holymusic.app.features.data.remote.model

import javax.annotation.concurrent.Immutable

@Immutable
data class TracksDtoItem(
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
    val id: String? = null,
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
    val albumId: String? = null,
    val isPremium: Boolean? = null
)

fun TracksDtoItem.toAlbumTrackDtoItem() : AlbumTrackDtoItem {
    return AlbumTrackDtoItem(
        about = about,
        appLanguage = appLanguage,
        artistId = artistId,
        artistName = artistName,
        category = category,
        categoryName = categoryName,
        contentBaseUrl = contentBaseUrl,
        contentCategory = contentCategory,
        createdBy = createdBy,
        createdOn = createdOn,
        duration = duration,
        genreId = genreId,
        trackId = id,
        imageUrl = imageUrl,
        isActive = isActive,
        lyrics = lyrics,
        sequenceNo = sequenceNo,
        streamUrl = streamUrl,
        subcategory = subcategory,
        subcategoryName = subcategoryName,
        trackName = title,
        updatedBy = updatedBy,
        updatedOn = updatedOn,
        albumId = albumId,
        isPremium = isPremium
    )
}