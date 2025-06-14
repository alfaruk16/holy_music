package com.holymusic.app.features.data.remote.entity

data class PlayCount(
    val AppLanguage: String,
    val ArtistId: String,
    val PlayInSecond: String,
    val StreamCount: String,
    val TrackId: String,
    val AppTypes: String = "ht"
)