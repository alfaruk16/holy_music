package com.holymusic.app.core.components.video_player

import com.holymusic.app.core.util.Enums
import com.holymusic.app.features.data.remote.model.AlbumDto
import com.holymusic.app.features.data.remote.model.AlbumTrackDto
import com.holymusic.app.features.data.remote.model.ArtistDto
import com.holymusic.app.features.data.remote.model.TracksDto
import com.holymusic.app.features.data.remote.model.TracksDtoItem


data class VideoPlayerState(
    val isLoading: Boolean = false,
    val artist: ArtistDto = ArtistDto(),
    val scholars: ArtistDto = ArtistDto(),
    val scholarTracks: TracksDto = TracksDto(),
    val artistTracks: TracksDto = TracksDto(),
    val albumTracks: AlbumTrackDto = AlbumTrackDto(),
    val albums: AlbumDto = AlbumDto(),
    val khatamTracks: TracksDto = TracksDto(),
    val downloadedTracks: TracksDto = TracksDto(),
    val showCountArtist: Int = 5,
    val showCountScholar: Int = 5,
    val showCountAlbum: Int = 5,
    val showCountKhatam: Int = 5,
    val showCountdownloads: Int = 5,
    val currentTrack: TracksDtoItem = TracksDtoItem(),
    val playingId: String = "",
    val isFavorite: Boolean = false,
    val favoriteLoading: Boolean = false,
    val downloadProgress: Int = 0,
    val isDownloaded: Boolean = false,
    val videoType: String = Enums.VideoType.Artist.name
)
