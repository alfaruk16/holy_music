package com.holymusic.app.features.data.remote.model

data class ScholarDtoData(
    val about: String? = null,
    val appLanguage: String? = null,
    val contentBaseUrl: String? = null,
    val createdBy: String? = null,
    val createdOn: String? = null,
    val id: String? = null,
    val imageUrl: String? = null,
    val institute: Any? = null,
    val isActive: Boolean? = null,
    val name: String? = null,
    val sequenceNo: Int? = null,
    val title: Any? = null,
    val totalFav: Int? = null,
    val totalTrack: Int? = null,
    val updatedBy: String? = null,
    val updatedOn: String? = null,
    val userFavouritedThis: Boolean? = null
)

fun ScholarDtoData.toArtistDtoItem(): ArtistDtoItem {
    return ArtistDtoItem(
        about = about,
        appLanguage = appLanguage,
        contentBaseUrl = contentBaseUrl,
        createdBy = createdBy,
        createdOn = createdOn,
        id = id,
        imageUrl = imageUrl,
        isActive = isActive,
        name = name,
        sequenceNo = sequenceNo,
        totalTrack = totalTrack,
        updatedBy = updatedBy,
        updatedOn = updatedOn
    )
}