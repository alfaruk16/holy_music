package com.holymusic.app.features.presentation.album_audios

import com.holymusic.app.features.data.remote.model.AlbumDto
import com.holymusic.app.features.data.remote.model.AlbumDtoItem
import com.holymusic.app.features.data.remote.model.AlbumTrackDto

data class AlbumAudiosState(
    val isLoading: Boolean = true,
    val tracks: AlbumTrackDto = AlbumTrackDto(),
    val currentAlbumId: String = "",
    val currentAlbum: AlbumDtoItem = AlbumDtoItem(),
    val albumList: AlbumDto = AlbumDto(),
    val playingId: Int = -1,
    val showCount: Int = 5
)