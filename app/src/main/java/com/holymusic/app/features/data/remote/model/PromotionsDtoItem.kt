package com.holymusic.app.features.data.remote.model

data class PromotionsDtoItem(
    val appLanguage: String,
    val appTypes: String,
    val contentId: String,
    val contentType: String,
    val createdBy: String,
    val createdOn: String,
    val endDate: Any,
    val hostName: String,
    val id: Int,
    val imageUrl: String,
    val isActive: Boolean,
    val startDate: Any,
    val updatedBy: Any,
    val updatedOn: String,
    val contentBaseUrl: String
)