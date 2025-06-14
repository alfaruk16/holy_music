package com.holymusic.app.features.data.remote.model

data class CategoryDtoItem(
    val name: String,
    val icon: Int,
    val isPopular: Boolean = false,
    val isFavorite: Boolean = false
)