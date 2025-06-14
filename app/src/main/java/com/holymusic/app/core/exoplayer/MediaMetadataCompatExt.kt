package com.holymusic.app.core.exoplayer

import android.support.v4.media.MediaMetadataCompat
import com.holymusic.app.core.exoplayer.data.entities.Song
import com.holymusic.app.features.data.remote.model.AlbumTrackDtoItem
import com.holymusic.app.features.data.remote.model.TracksDtoItem

fun MediaMetadataCompat.toSong(): Song? {
    return description?.let {
        Song(
            mediaId = it.mediaId ?: "",
            artistId = it.description.toString(),
            title = it.title.toString(),
            subtitle = it.subtitle.toString(),
            songUrl = it.mediaUri.toString(),
            imageUrl = it.iconUri.toString()
        )
    }
}

fun TracksDtoItem.toSong(): Song {
    return Song(
        mediaId = id.toString(),
        artistId = artistId.toString(),
        title = title.toString(),
        subtitle = artistName.toString(),
        imageUrl = "$contentBaseUrl/$imageUrl",
        songUrl = streamUrl.toString()
    )
}

fun AlbumTrackDtoItem.toSong(): Song {
    return Song(
        mediaId = id.toString(),
        artistId = artistId.toString(),
        title = trackName.toString(),
        subtitle = artistName.toString(),
        imageUrl = "$contentBaseUrl/$imageUrl",
        songUrl = streamUrl.toString()
    )
}

//fun MediaMetadataCompat.toTrack(): TracksDtoItem? {
//    return description?.let {
//        TracksDtoItem(
//            id = it.mediaId.toString(),
//            artistId = it.description.toString(),
//            title = it.title.toString(),
//            about = it.subtitle.toString(),
//            streamUrl = it.mediaUri.toString(),
//            imageUrl = it.iconUri.toString()
//        )
//    }
//}