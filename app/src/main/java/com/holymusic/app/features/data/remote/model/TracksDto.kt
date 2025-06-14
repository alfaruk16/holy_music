package com.holymusic.app.features.data.remote.model

import javax.annotation.concurrent.Immutable

@Immutable
data class TracksDto(
    val `data`: List<TracksDtoItem>? = null,
    val error: Any? = null,
    val message: String? = null,
    val status: Int? = null,
    val totalPage: Int? = null,
    val totalRecords: Int? = null
)