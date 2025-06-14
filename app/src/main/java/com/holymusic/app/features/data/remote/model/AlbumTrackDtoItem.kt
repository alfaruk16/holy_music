package com.holymusic.app.features.data.remote.model

import javax.annotation.concurrent.Immutable

@Immutable
data class AlbumTrackDtoItem(
    val about: String? = null,
    val albumId: String? = null,
    val albumName: String? = null,
    val appLanguage: String? = null,
    val artistAppearsAs: Any? = null,
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
    val isPublish: Boolean? = null,
    val lyrics: String? = null,
    val sequenceNo: Int? = null,
    val streamUrl: String? = null,
    val subcategory: String? = null,
    val subcategoryName: String? = null,
    val trackAlbum: Any? = null,
    val trackId: String? = null,
    val trackName: String? = null,
    val updatedBy: String? = null,
    val updatedOn: String? = null,
    val playCount: Int? = null,
    val isPremium: Boolean? = null
)


fun AlbumTrackDtoItem.toTrackDtoItem(): TracksDtoItem {
    return TracksDtoItem(
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
        id = trackId,
        imageUrl = imageUrl,
        isActive = isActive,
        lyrics = lyrics,
        sequenceNo = sequenceNo,
        streamUrl = streamUrl,
        subcategory = subcategory,
        subcategoryName = subcategoryName,
        title = trackName,
        updatedBy = updatedBy,
        updatedOn = updatedOn,
        playCount = playCount
    )
}