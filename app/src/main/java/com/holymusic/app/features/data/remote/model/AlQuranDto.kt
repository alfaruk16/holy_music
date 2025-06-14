package com.holymusic.app.features.data.remote.model

data class AlQuranDto(
    val `data`: List<AlQuranDtoItem>? = null,
    val error: Any? = null,
    val message: String? = null,
    val status: Int? = null,
    val totalPage: Int? = null,
    val totalRecords: Int? = null
)