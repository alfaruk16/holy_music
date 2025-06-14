package com.holymusic.app.features.data.remote.model

import javax.annotation.concurrent.Immutable

@Immutable
data class TrackBillboardDtoItem(
    val about: String? = null,
    val appLanguage: String? = null,
    val contentBaseUrl: String? = null,
    val contentCategory: String? = null,
    val contentType: String? = null,
    val createdBy: String? = null,
    val createdOn: String? = null,
    val id: String? = null,
    val imageUrl: String? = null,
    val isActive: Boolean? = null,
    val sequenceNo: Int? = null,
    val shortStreamUrl: String? = null,
    val targetId: String? = null,
    val title: String? = null,
    val updatedBy: String? = null,
    val updatedOn: String? = null
)

fun TrackBillboardDtoItem.toTrackDtoItem(): TracksDtoItem {
    return TracksDtoItem(
        about = about,
        appLanguage = appLanguage,
        contentBaseUrl = contentBaseUrl,
        contentCategory = contentCategory,
        createdBy = createdBy,
        createdOn = createdOn,
        id = targetId,
        imageUrl = imageUrl,
        isActive = isActive,
        sequenceNo = sequenceNo,
        streamUrl = shortStreamUrl,
        title = title,
        updatedBy = updatedBy,
        updatedOn = updatedOn
    )
}