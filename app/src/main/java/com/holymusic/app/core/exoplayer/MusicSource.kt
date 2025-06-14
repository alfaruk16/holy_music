package com.holymusic.app.core.exoplayer

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ARTIST
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_URI
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE
import androidx.core.net.toUri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.holymusic.app.core.exoplayer.State.STATE_CREATED
import com.holymusic.app.core.exoplayer.State.STATE_ERROR
import com.holymusic.app.core.exoplayer.State.STATE_INITIALIZED
import com.holymusic.app.core.exoplayer.State.STATE_INITIALIZING
import com.holymusic.app.core.theme.replaceSize
import com.holymusic.app.core.theme.seventyTwo
import com.holymusic.app.core.util.AppConstants
import com.holymusic.app.features.domain.repository.ApiRepo
import com.holymusic.app.features.domain.repository.TrackRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MusicSource @Inject constructor(
    private val apiRepo: ApiRepo,
    private val trackRepo: TrackRepo
) {

    var songs = emptyList<MediaMetadataCompat>()

    suspend fun fetchMediaData() = withContext(Dispatchers.IO) {
        state = STATE_INITIALIZING

        val allSongs = trackRepo.getTracks().ifEmpty {
            apiRepo.getTracksByType(AppConstants.typeAudio).data
                ?.filter { it.isActive ?: false }.also { trackRepo.createTracks(it ?: emptyList()) }
        } ?: emptyList()

        songs = allSongs.map { song ->
            MediaMetadataCompat.Builder()
                .putString(METADATA_KEY_ARTIST, song.artistName.toString())
                .putString(METADATA_KEY_MEDIA_ID, song.id.toString())
                .putString(METADATA_KEY_TITLE, song.title.toString())
                .putString(METADATA_KEY_DISPLAY_TITLE, song.title.toString())
                .putString(
                    METADATA_KEY_DISPLAY_ICON_URI,
                    song.contentBaseUrl + replaceSize(song.imageUrl ?: "", seventyTwo)
                )
                .putString(METADATA_KEY_MEDIA_URI, song.contentBaseUrl + song.streamUrl)
                .putString(METADATA_KEY_ALBUM_ART_URI, song.contentBaseUrl + song.imageUrl)
                .putString(METADATA_KEY_DISPLAY_SUBTITLE, song.artistName.toString())
                .putString(METADATA_KEY_DISPLAY_DESCRIPTION, song.artistId.toString())
                .build()
        }
        state = STATE_INITIALIZED
    }

    fun asMediaSource(dataSourceFactory: DefaultDataSourceFactory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.forEach { song ->
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(
                    MediaItem.fromUri(
                        song.getString(METADATA_KEY_MEDIA_URI).toUri()
                    )
                )
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    fun asMediaItems() = songs.map { song ->
        val desc = MediaDescriptionCompat.Builder()
            .setMediaUri(song.getString(METADATA_KEY_MEDIA_URI).toUri())
            .setTitle(song.description.title)
            .setSubtitle(song.description.subtitle)
            .setMediaId(song.description.mediaId)
            .setIconUri(song.description.iconUri)
            .build()
        MediaBrowserCompat.MediaItem(desc, FLAG_PLAYABLE)
    }.toMutableList()

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    private var state: State = STATE_CREATED
        set(value) {
            if (value == STATE_INITIALIZED || value == STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach { listener ->
                        listener(state == STATE_INITIALIZED)
                    }
                }
            } else {
                field = value
            }
        }

    fun whenReady(action: (Boolean) -> Unit): Boolean {
        return if (state == STATE_CREATED || state == STATE_INITIALIZING) {
            onReadyListeners += action
            false
        } else {
            action(state == STATE_INITIALIZED)
            true
        }
    }
}

enum class State {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}















