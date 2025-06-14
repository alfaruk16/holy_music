package com.holymusic.app.features.presentation.video_album

import com.holymusic.app.features.data.remote.model.AlbumDto
import com.holymusic.app.features.data.remote.model.TrackBillboardDto
import com.holymusic.app.features.data.remote.model.TracksDtoItem

data class VideoAlbumState(
    val isLoading: Boolean = true,
    val albums: AlbumDto = AlbumDto(),
    val trackBillboard: TrackBillboardDto = TrackBillboardDto(),
    val playingId: Int = -1,
    val showCount: Int = 5,
    val currentTrack: TracksDtoItem = TracksDtoItem()
)