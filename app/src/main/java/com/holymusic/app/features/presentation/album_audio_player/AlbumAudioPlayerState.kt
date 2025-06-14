package com.holymusic.app.features.presentation.album_audio_player

import com.holymusic.app.features.data.remote.model.AlbumDto
import com.holymusic.app.features.data.remote.model.AlbumTrackDto
import com.holymusic.app.features.data.remote.model.AlbumTrackDtoItem

data class AlbumAudioPlayerState(
    val isLoading: Boolean = false,
    val track: AlbumTrackDtoItem = AlbumTrackDtoItem(),
    val playingId: Int = -1,
    val tracks: AlbumTrackDto = AlbumTrackDto(),
    val albumList: AlbumDto = AlbumDto(),
    val currentArtistId: String = "",
    val showCount: Int = 5,
    val isFavorite: Boolean = false,
    val favoriteLoading: Boolean = false,
    val downloadProgress: Int = 0,
    val isDownloaded: Boolean = false
)