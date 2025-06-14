package com.holymusic.app.features.data.remote.model

data class DefaultDto(
    val `data`: Any? = null,
    val error: String? = null,
    val message: String? = null,
    val status: Int? = null,
    val totalPage: Int? = null,
    val totalRecords: Int? = null
)