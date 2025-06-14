package com.holymusic.app.features.data.remote.model

data class ContentDtoData(
    val about: String? = null,
    val contentBaseUrl: String? = null,
    val items: List<ContentDtoItem>? = null,
    val order: Int? = null,
    val patchId: String? = null,
    val patchImageUrl: String? = null,
    val patchName: String? = null,
    val patchViewType: String? = null
)