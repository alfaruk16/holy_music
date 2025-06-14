package com.holymusic.app.features.data.remote.model

data class ScholarTrackDto(
    val `data`: TracksDtoItem,
    val error: Any,
    val message: String,
    val status: Int,
    val totalPage: Int,
    val totalRecords: Int
)