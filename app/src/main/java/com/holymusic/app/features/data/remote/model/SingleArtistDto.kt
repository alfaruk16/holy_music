package com.holymusic.app.features.data.remote.model

data class SingleArtistDto(
    val `data`: ArtistDtoItem,
    val error: Any,
    val message: String,
    val status: Int,
    val totalPage: Int,
    val totalRecords: Int
)