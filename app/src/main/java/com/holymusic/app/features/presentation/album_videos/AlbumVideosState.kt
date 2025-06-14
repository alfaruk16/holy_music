package com.holymusic.app.features.presentation.album_videos

import com.holymusic.app.features.data.remote.model.AlbumDto
import com.holymusic.app.features.data.remote.model.AlbumDtoItem
import com.holymusic.app.features.data.remote.model.AlbumTrackDto
import com.holymusic.app.features.data.remote.model.TracksDtoItem

data class AlbumVideosState(
    val isLoading: Boolean = true,
    val tracks: AlbumTrackDto = AlbumTrackDto(),
    val currentAlbumId: String = "",
    val currentAlbum: AlbumDtoItem = AlbumDtoItem(),
    val albumList: AlbumDto = AlbumDto(),
    val playingId: String = "",
    val showCount: Int = 5,
    val currentTrack: TracksDtoItem = TracksDtoItem()
)