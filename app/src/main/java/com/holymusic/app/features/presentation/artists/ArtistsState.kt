package com.holymusic.app.features.presentation.artists

import com.holymusic.app.features.data.remote.model.ArtistDto

data class ArtistsState(
    val isLoading: Boolean = true,
    val artistList: ArtistDto = ArtistDto()
)