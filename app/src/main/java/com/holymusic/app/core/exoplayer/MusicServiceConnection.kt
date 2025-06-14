package com.holymusic.app.core.exoplayer

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.holymusic.app.core.exoplayer.other.Constants.NETWORK_ERROR
import com.holymusic.app.core.exoplayer.other.Event
import com.holymusic.app.core.exoplayer.other.Resource
import com.holymusic.app.features.domain.repository.ApiRepo
import com.holymusic.app.features.domain.repository.DownloadRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MusicServiceConnection(
    context: Context,
    private val apiRepo: ApiRepo,
    private val trackRepo: DownloadRepo
) {
    private val _isConnected = MutableLiveData<Event<Resource<Boolean>>>()
    val isConnected: LiveData<Event<Resource<Boolean>>> = _isConnected

    private val _networkError = MutableLiveData<Event<Resource<Boolean>>>()
    val networkError: LiveData<Event<Resource<Boolean>>> = _networkError

    private val _playbackState = MutableLiveData<PlaybackStateCompat?>()
    val playbackState: LiveData<PlaybackStateCompat?> = _playbackState

    private val _curPlayingSong = MutableLiveData<MediaMetadataCompat?>()
    val curPlayingSong: LiveData<MediaMetadataCompat?> = _curPlayingSong

    val _played = MutableLiveData<Boolean?>()
    val played: LiveData<Boolean?> = _played

    val _favorite = MutableLiveData<Boolean?>()
    val favorite: LiveData<Boolean?> = _favorite

    lateinit var mediaController: MediaControllerCompat

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)

    var mediaBrowser: MediaBrowserCompat

    private val ctx = context

    private val _isRepeat = MutableLiveData(false)
    val isRepeat: LiveData<Boolean> = _isRepeat

    private val _isShuffle = MutableLiveData(false)
    val isShuffle: LiveData<Boolean> = _isShuffle

    val _downloadProgress = MutableLiveData(0)
    val downloadProgress: LiveData<Int> = _downloadProgress

    private val _isVideoPlaying = MutableLiveData<Boolean?>()
    val isVideoPlaying: LiveData<Boolean?> = _isVideoPlaying

    private val serviceScope = CoroutineScope(Dispatchers.Main)

    init {
        mediaBrowser = MediaBrowserCompat(
            context,
            ComponentName(
                context,
                MusicService::class.java
            ),
            mediaBrowserConnectionCallback,
            null
        ).apply { connect() }
    }

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.subscribe(parentId, callback)
    }

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.unsubscribe(parentId, callback)
    }

    fun enableRepeatMode() {
        _isRepeat.value = true
        transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE)
        if (isShuffle.value == true) {
            disableShuffle()
        }
    }

    fun disableRepeatMode() {
        _isRepeat.value = false
        transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE)
    }

    fun enableShuffle() {
        _isShuffle.value = true
        transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL)
        if (isRepeat.value == true) {
            disableRepeatMode()
        }
    }

    fun disableShuffle() {
        _isShuffle.value = false
        transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE)
    }
    private inner class MediaBrowserConnectionCallback(
        private val context: Context
    ) : MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            println("MusicServiceConnection CONNECTED")
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaContollerCallback())
            }
            _isConnected.postValue(Event(Resource.success(true)))
        }

        override fun onConnectionSuspended() {
            println("MusicServiceConnection SUSPENDED")

            _isConnected.postValue(
                Event(
                    Resource.error(
                        "The connection was suspended", false
                    )
                )
            )
        }

        override fun onConnectionFailed() {
            println("MusicServiceConnection FAILED")

            _isConnected.postValue(
                Event(
                    Resource.error(
                        "Couldn't connect to media browser", false
                    )
                )
            )
        }
    }

    private inner class MediaContollerCallback : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            _playbackState.postValue(state)
            if (state?.state == 3 && played.value != true) {
                _played.postValue(true)
                MusicService.played = true
            }
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            if (curPlayingSong.value?.toSong()?.mediaId != metadata?.toSong()?.mediaId) {
                checkIsFavorite(metadata?.description?.mediaId)
                checkIsDownloaded(metadata?.description?.mediaId)
            }
            _curPlayingSong.postValue(metadata)
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            when (event) {
                NETWORK_ERROR -> _networkError.postValue(
                    Event(
                        Resource.error(
                            "Couldn't connect to the server. Please check your internet connection.",
                            null
                        )
                    )
                )
            }
        }

        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
            serviceScope.cancel()
        }
    }

    private fun checkIsFavorite(mediaId: String?) {
        if (mediaId != null) {
            serviceScope.launch {
                try {
                    val isFavorite = apiRepo.isFavorite(mediaId)
                    if (isFavorite.data == true) {
                        _favorite.postValue(true)
                    } else {
                        _favorite.postValue(false)
                    }

                } catch (_: HttpException) {
                    _favorite.postValue(false)
                } catch (_: IOException) {
                    _favorite.postValue(false)
                }
            }
        }
    }

    fun checkIsDownloaded(id: String?) {
        serviceScope.launch {
            val isDownloaded = trackRepo.isDownloaded(id ?: "")
            if(isDownloaded){
                _downloadProgress.postValue(100)
            }else{
                _downloadProgress.postValue(0)
            }
        }
    }


    fun release() {
        mediaBrowser.disconnect()
    }

    fun isVideoPlaying(playing: Boolean) {
        _isVideoPlaying.postValue(playing)
    }

    fun connect() {
        mediaBrowser = MediaBrowserCompat(
            ctx,
            ComponentName(
                ctx,
                MusicService::class.java
            ),
            mediaBrowserConnectionCallback,
            null
        ).apply { connect() }
    }
}

















