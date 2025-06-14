package com.holymusic.app.features.presentation.my_favorites

import com.holymusic.app.features.data.remote.model.TracksDto
import com.holymusic.app.features.data.remote.model.TracksDtoItem

data class MyFavoritesState(
    val isLoading: Boolean = true,
    val tracks: TracksDto = TracksDto(),
    val videoTracks: TracksDto = TracksDto(),
    val playingId: Int = -1,
    val showCount: Int = 5,
    val showCountVideo: Int = 5,
    val selectedTab: Int = 0,
    val currentTrack: TracksDtoItem = TracksDtoItem()
)