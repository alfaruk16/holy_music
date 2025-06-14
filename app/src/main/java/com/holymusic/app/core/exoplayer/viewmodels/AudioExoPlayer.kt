package com.holymusic.app.core.exoplayer.viewmodels

import android.app.Activity
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.holymusic.app.core.exoplayer.currentPlaybackPosition
import com.holymusic.app.core.exoplayer.isPlayEnabled
import com.holymusic.app.core.exoplayer.isPlaying
import com.holymusic.app.core.exoplayer.isPrepared
import com.holymusic.app.core.exoplayer.MusicService
import com.holymusic.app.core.exoplayer.MusicServiceConnection
import com.holymusic.app.core.exoplayer.data.entities.Song
import com.holymusic.app.core.exoplayer.other.Constants.MEDIA_ROOT_ID
import com.holymusic.app.core.exoplayer.other.Resource
import com.holymusic.app.core.exoplayer.toSong
import com.holymusic.app.core.util.AppConstants
import com.holymusic.app.core.util.Async
import com.holymusic.app.core.util.AudioPlayer
import com.holymusic.app.features.data.local.model.FileItem
import com.holymusic.app.features.data.remote.entity.Favorite
import com.holymusic.app.features.data.remote.entity.PlayCount
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.domain.repository.DownloadRepo
import com.holymusic.app.features.domain.services.AndroidDownloader
import com.holymusic.app.features.domain.use_case.AddPlayCountUseCase
import com.holymusic.app.features.domain.use_case.CancelFavoriteUseCase
import com.holymusic.app.features.domain.use_case.SetFavoriteUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class AudioExoPlayer @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val addPlayCountUseCase: AddPlayCountUseCase,
    private val setFavoriteUseCase: SetFavoriteUseCase,
    private val cancelFavoriteUseCase: CancelFavoriteUseCase,
    private val trackRepo: DownloadRepo,
    private val audioPlayer: AudioPlayer,
    private val downloader: AndroidDownloader
) : ViewModel() {

    private val _mediaItems = MutableLiveData<Resource<List<Song>>>()
    val mediaItems: LiveData<Resource<List<Song>>> = _mediaItems

    private val isConnected = musicServiceConnection.isConnected

    //    val networkError = musicServiceConnection.networkError
    val curPlayingSong = musicServiceConnection.curPlayingSong
    val playbackState = musicServiceConnection.playbackState
    val played = musicServiceConnection.played
    val isRepeat = musicServiceConnection.isRepeat
    val isShuffle = musicServiceConnection.isShuffle
    val isFavorite = musicServiceConnection.favorite
    val isVideoPlaying = musicServiceConnection.isVideoPlaying

    private val _curSongDuration = MutableLiveData<Long>()
    val curSongDuration: LiveData<Long> = _curSongDuration

    private val _curPlayerPosition = MutableLiveData<Long?>()
    val curPlayerPosition: LiveData<Long?> = _curPlayerPosition

    private var addedPlayCount = ""
    private var favoriteLoading = false

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    val downloadProgress = musicServiceConnection.downloadProgress

    init {
        if (!musicServiceConnection.isConnected.isInitialized) {
            musicServiceConnection.release()
            musicServiceConnection.connect()
        }
        //initMediaItems()
    }
//     fun initMediaItems(){
//        _mediaItems.postValue(Resource.loading(null))
//        musicServiceConnection.subscribe(
//            MEDIA_ROOT_ID,
//            object : MediaBrowserCompat.SubscriptionCallback() {
//                override fun onChildrenLoaded(
//                    parentId: String,
//                    children: MutableList<MediaBrowserCompat.MediaItem>
//                ) {
//                    super.onChildrenLoaded(parentId, children)
//                    val items = children.map {
//                        Song(
//                            it.mediaId.toString(),
//                            it.description.title.toString(),
//                            it.description.subtitle.toString(),
//                            it.description.mediaUri.toString(),
//                            it.description.iconUri.toString()
//                        )
//                    }
//                    _mediaItems.postValue(Resource.success(items))
//                }
//            })
//    }

    fun updateCurrentPlayerPosition() {
        viewModelScope.launch {
            val pos = playbackState.value?.currentPlaybackPosition
            if (curPlayerPosition.value != pos && playbackState.value?.isPlaying == true) {
                _curPlayerPosition.postValue(pos)
                if (MusicService.curSongDuration > 0) {
                    _curSongDuration.postValue(MusicService.curSongDuration)
                }
                if (pos != null && curSongDuration.value != null && curSongDuration.value?.div(1000) == pos / 1000) {
                    addPlayCount()
                }
            }
        }
    }

    fun skipToNextSong() {
        addPlayCount()
        musicServiceConnection.transportControls.skipToNext()
        _curPlayerPosition.postValue(0)
    }

    fun skipToPreviousSong() {
        addPlayCount()
        musicServiceConnection.transportControls.skipToPrevious()
        _curPlayerPosition.postValue(0)
    }

    fun seekTo(pos: Long) {
        musicServiceConnection.transportControls.seekTo(pos)
        _curPlayerPosition.postValue(pos)
    }

    fun playOrToggleSong(mediaItem: Song, toggle: Boolean = false) {
        addPlayCount()
        val isPrepared = playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaItem.mediaId ==
            curPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)
        ) {
            playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> if (toggle) musicServiceConnection.transportControls.pause()
                    playbackState.isPlayEnabled -> musicServiceConnection.transportControls.play()
                    else -> Unit
                }
            }
        } else {
            if (isConnected.isInitialized) {
                musicServiceConnection.transportControls.playFromMediaId(mediaItem.mediaId, null)
            }
        }
        if (audioPlayer.isPlaying()) {
            audioPlayer.pause()
        }
    }

    fun pause() {
        if (playbackState.value?.isPlaying == true) {
            musicServiceConnection.transportControls.pause()
        }
    }

    fun close() {
        viewModelScope.launch {
            if (musicServiceConnection._played.value == true) {
                musicServiceConnection._played.postValue(false)
                musicServiceConnection.transportControls.pause()
                seekTo(0)
            }
        }
    }

    fun addPlayCount() {
        if (curPlayerPosition.value != null && (curPlayerPosition.value?.div(1000)
                ?: 0) >= 15 && curPlayingSong.value?.toSong()?.mediaId != addedPlayCount
        ) {

            val requestBody: RequestBody = Gson().toJson(
                curPlayingSong.value?.toSong()?.let {
                    PlayCount(
                        AppLanguage = AppConstants.language,
                        ArtistId = it.artistId,
                        PlayInSecond = (curPlayerPosition.value?.div(1000))?.toInt().toString(),
                        StreamCount = "1",
                        TrackId = it.mediaId
                    )
                }
            )
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())

            addPlayCountUseCase(requestBody).onEach { result ->

                when (result) {
                    is Async.Success -> {
                        println("Success")
                        addedPlayCount = curPlayingSong.value?.toSong()?.mediaId ?: ""
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        }
    }

//    fun release() {
//        musicServiceConnection.release()
//    }
//
//    fun connect() {
//        musicServiceConnection.connect()
//    }


    fun enableRepeatMode() {
        musicServiceConnection.enableRepeatMode()
    }

    fun disableRepeatMode() {
        musicServiceConnection.disableRepeatMode()
    }

    fun enableShuffle() {
        musicServiceConnection.enableShuffle()
    }

    fun disableShuffle() {
        musicServiceConnection.disableShuffle()
    }

    fun setFavorite(track: TracksDtoItem) {
        if (!favoriteLoading) {
            if (isFavorite.value == true) {
                cancelFavorite(track)
            } else {
                addFavorite(track)
            }
        }
    }

    private fun addFavorite(track: TracksDtoItem) {
        setFavoriteUseCase.invoke(Favorite(track.id ?: "", track.artistId ?: "0"))
            .onEach { result ->
                favoriteLoading = when (result) {
                    is Async.Success -> {
                        musicServiceConnection._favorite.postValue(true)
                        false
                    }

                    is Async.Loading -> true
                    else -> {
                        false
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun cancelFavorite(track: TracksDtoItem) {
        cancelFavoriteUseCase.invoke(track.id ?: "")
            .onEach { result ->
                favoriteLoading = when (result) {
                    is Async.Success -> {
                        musicServiceConnection._favorite.postValue(false)
                        false
                    }

                    is Async.Loading -> true

                    else -> {
                        false
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun updateFavorite(isFavorite: Boolean) {
        musicServiceConnection._favorite.postValue(isFavorite)
    }

    fun checkIsDownloaded(id: String) {
        musicServiceConnection.checkIsDownloaded(id)
    }

    fun download(activity: Activity, file: FileItem) {
        serviceScope.launch {
            if (downloadProgress.value == 0) {
                downloader.downloadFile(activity, file, progress = {
                    musicServiceConnection._downloadProgress.postValue(it)
                })
            }
        }
    }

    fun isVideoPlaying(playing: Boolean) {
        musicServiceConnection.isVideoPlaying(playing)
    }

    override fun onCleared() {
        super.onCleared()
        addPlayCount()
        musicServiceConnection.unsubscribe(
            MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {})
    }

}

















