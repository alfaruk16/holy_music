package com.holymusic.app.features.presentation.video_scholars

import com.holymusic.app.features.data.remote.model.ArtistDto
import com.holymusic.app.features.data.remote.model.TracksDto
import com.holymusic.app.features.data.remote.model.TracksDtoItem

data class VideoScholarState(
    val isLoading: Boolean = true,
    val artist: ArtistDto = ArtistDto(),
    val tracks: TracksDto = TracksDto(),
    val playingId: String = "",
    val showCount: Int = 5,
    val currentTrack: TracksDtoItem = TracksDtoItem()
)