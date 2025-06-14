package com.holymusic.app.features.data.remote.model

data class PrayerTimesDto(
    val `data`: List<PrayerTimesDtoItem>? = null,
    val error: Any? = null,
    val message: String? = null,
    val status: Int? = null,
    val totalPage: Int? = null,
    val totalRecords: Int? = null
)