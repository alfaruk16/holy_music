package com.holymusic.app.features.presentation.downloads

import com.holymusic.app.features.data.remote.model.TracksDto
import com.holymusic.app.features.data.remote.model.TracksDtoItem

data class DownloadsState(
    val isLoading: Boolean = false,
    val tracks: TracksDto = TracksDto(),
    val videoTracks: TracksDto = TracksDto(),
    val playingId: String = "",
    val showCount: Int = 5,
    val showCountVideo: Int = 5,
    val selectedTab: Int = 0,
    val message: String = "",
    val showSnackBar: Boolean = false,
    val currentTrack: TracksDtoItem = TracksDtoItem()
)