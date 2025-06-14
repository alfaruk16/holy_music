package com.holymusic.app.features.presentation.audio_artists

import com.holymusic.app.features.data.remote.model.ArtistDto
import com.holymusic.app.features.data.remote.model.TrackBillboardDto
import com.holymusic.app.features.data.remote.model.TracksDto

data class AudioArtistState(
    val isLoading: Boolean = true,
    val artist: ArtistDto = ArtistDto(),
    val tracks: TracksDto = TracksDto(),
    val trackBillboard: TrackBillboardDto = TrackBillboardDto(),
    val playingId: Int = -1,
    val showCount: Int = 0,
    val page: Int = 1,
    val take: Int = 50
)