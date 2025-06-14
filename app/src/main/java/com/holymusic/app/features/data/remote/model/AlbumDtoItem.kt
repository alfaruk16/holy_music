package com.holymusic.app.features.data.remote.model

import javax.annotation.concurrent.Immutable

@Immutable
data class AlbumDtoItem(
    val about: String? = null,
    val albumContents: Any? = null,
    val appLanguage: String? = null,
    val artistAppearsAs: String? = null,
    val category: String? = null,
    val categoryName: String? = null,
    val contentBaseUrl: String? = null,
    val contentCategory: String? = null,
    val copyright: String? = null,
    val createdBy: String? = null,
    val createdOn: String? = null,
    val id: String? = null,
    val imageUrl: String? = null,
    val isActive: Boolean? = null,
    val labelName: String? = null,
    val sequenceNo: Int? = null,
    val subcategory: Any? = null,
    val subcategoryName: Any? = null,
    val title: String? = null,
    val totalTrack: Int? = null,
    val updatedBy: String? = null,
    val updatedOn: String? = null
)