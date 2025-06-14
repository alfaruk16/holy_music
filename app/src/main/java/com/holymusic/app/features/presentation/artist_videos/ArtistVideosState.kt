package com.holymusic.app.features.presentation.artist_videos

import com.holymusic.app.features.data.remote.model.ArtistDto
import com.holymusic.app.features.data.remote.model.ArtistDtoItem
import com.holymusic.app.features.data.remote.model.TracksDto
import com.holymusic.app.features.data.remote.model.TracksDtoItem

data class ArtistVideosState(
    val isLoading: Boolean = true,
    val tracks: TracksDto = TracksDto(),
    val videoTracks: TracksDto = TracksDto(),
    val currentArtistId: String = "",
    val currentArtist: ArtistDtoItem = ArtistDtoItem(),
    val artistList: ArtistDto = ArtistDto(),
    val playingId: Int = -1,
    val showCount: Int = 5,
    val showCountVideo: Int = 5,
    val selectedTab: Int = 1,
    val currentTrack: TracksDtoItem = TracksDtoItem()
)