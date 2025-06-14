package com.holymusic.app.features.data.remote.model

data class SubCategoryContentsDto(
    val `data`: List<SubCategoryContentsDtoItem>? = null,
    val error: Any? = null,
    val message: String? = null,
    val status: Int? = null,
    val totalPage: Int? = null,
    val totalRecords: Int? = null
)