package com.holymusic.app.features.data.remote.model

import javax.annotation.concurrent.Immutable

@Immutable
data class ArtistDtoItem(
    val about: String? = null,
    val appLanguage: String? = null,
    val contentBaseUrl: String? = null,
    val contentUrl: Any? = null,
    val createdBy: String? = null,
    val createdOn: String? = null,
    val id: String? = null,
    val imageUrl: String? = null,
    val isActive: Boolean? = null,
    val name: String? = null,
    val sequenceNo: Int? = null,
    val updatedBy: String? = null,
    val updatedOn: String? = null,
    val totalTrack: Int? = null
)