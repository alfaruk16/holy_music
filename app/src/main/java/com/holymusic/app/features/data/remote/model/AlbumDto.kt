package com.holymusic.app.features.data.remote.model

import javax.annotation.concurrent.Immutable

@Immutable
data class AlbumDto(
    val `data`: List<AlbumDtoItem>? = null,
    val error: Any? = null,
    val message: String? = null,
    val status: Int? = null,
    val totalPage: Int? = null,
    val totalRecords: Int? = null
)