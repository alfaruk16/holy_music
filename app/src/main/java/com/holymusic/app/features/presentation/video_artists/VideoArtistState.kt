package com.holymusic.app.features.presentation.video_artists

import com.holymusic.app.features.data.remote.model.ArtistDto
import com.holymusic.app.features.data.remote.model.TrackBillboardDto
import com.holymusic.app.features.data.remote.model.TracksDto
import com.holymusic.app.features.data.remote.model.TracksDtoItem

data class VideoArtistState(
    val isLoading: Boolean = true,
    val artist: ArtistDto = ArtistDto(),
    val tracks: TracksDto = TracksDto(),
    val trackBillboard: TrackBillboardDto = TrackBillboardDto(),
    val playingId: String = "",
    val showCount: Int = 0,
    val page : Int = 1,
    val take: Int = 50,
    val currentTrack: TracksDtoItem = TracksDtoItem()
)