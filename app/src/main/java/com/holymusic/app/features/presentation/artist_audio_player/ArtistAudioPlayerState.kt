package com.holymusic.app.features.presentation.artist_audio_player

import com.holymusic.app.features.data.remote.model.ArtistDto
import com.holymusic.app.features.data.remote.model.TracksDto
import com.holymusic.app.features.data.remote.model.TracksDtoItem

data class ArtistAudioPlayerState(
    val isLoading: Boolean = false,
    val track: TracksDtoItem = TracksDtoItem(),
    val playingId: Int = -1,
    val tracks: TracksDto = TracksDto(),
    val artistList: ArtistDto = ArtistDto(),
    val currentArtistId: String = "",
    val showCount: Int = 5,
    val isFavorite: Boolean = false,
    val favoriteLoading: Boolean = false,
    val downloadProgress: Int = 0,
    val isDownloaded: Boolean = false
)