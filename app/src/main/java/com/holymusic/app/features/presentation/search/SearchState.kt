package com.holymusic.app.features.presentation.search

import com.holymusic.app.features.data.remote.model.ArtistDto
import com.holymusic.app.features.data.remote.model.TracksDto
import com.holymusic.app.features.data.remote.model.TracksDtoItem

data class SearchState(
    val isLoading: Boolean = true,
    val artist: ArtistDto = ArtistDto(),
    val searchedArtist: ArtistDto = ArtistDto(),
    val tracks: TracksDto = TracksDto(),
    val searchedTracks: TracksDto = TracksDto(),
    val videoTracks: TracksDto = TracksDto(),
    val searchedVideoTracks: TracksDto = TracksDto(),
    val playingId: Int = -1,
    val showCount: Int = 5,
    val showCountVideo: Int = 5,
    val search: String = "",
    val selectedTab: Int = 0,
    val currentTrack: TracksDtoItem = TracksDtoItem()
)