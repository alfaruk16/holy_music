package com.holymusic.app.features.presentation.audio_album

import com.holymusic.app.features.data.remote.model.AlbumDto
import com.holymusic.app.features.data.remote.model.TrackBillboardDto

data class AudioAlbumState(
    val isLoading: Boolean = true,
    val albums: AlbumDto = AlbumDto(),
    val trackBillboard: TrackBillboardDto = TrackBillboardDto(),
    val playingId: Int = -1,
    val showCount: Int = 5,
)