package com.holymusic.app.features.presentation.video_album

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.holymusic.app.core.exoplayer.isPlaying
import com.holymusic.app.core.exoplayer.data.entities.Song
import com.holymusic.app.core.exoplayer.toSong
import com.holymusic.app.core.exoplayer.viewmodels.AudioExoPlayer
import com.holymusic.app.core.util.AppConstants
import com.holymusic.app.core.util.Async
import com.holymusic.app.features.data.remote.model.AlbumDto
import com.holymusic.app.features.data.remote.model.TrackBillboardDto
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.domain.use_case.GetAlbumByTypeUseCase
import com.holymusic.app.features.domain.use_case.GetTrackBillboardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoAlbumViewModel @Inject constructor(
    private val getAlbumByTypeUseCase: GetAlbumByTypeUseCase,
    val audioExoPlayer: AudioExoPlayer,
    private val getTrackBillboardUseCase: GetTrackBillboardUseCase,
    savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    private val _state = MutableStateFlow(VideoAlbumState())
    val state: StateFlow<VideoAlbumState> = _state.asStateFlow()

    private var curPlaying: Song? = null

    init {
        getAlbum()
        getTrackBillboard()
        subscribeToObservers()
    }

    private fun getTrackBillboard() {

        getTrackBillboardUseCase().onEach { result ->
            when (result) {
                is Async.Success -> {

                    val list = result.data?.data?.filter { it.contentType == AppConstants.trackType && it.contentCategory == AppConstants.typeVideo }

                    _state.value = state.value.copy(
                        trackBillboard = result.data?.copy(data = list) ?: TrackBillboardDto(),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun getAlbum() {
        getAlbumByTypeUseCase(AppConstants.typeVideo).onEach { result ->
            when (result) {
                is Async.Success -> {

                    _state.value = state.value.copy(
                        albums = result.data ?: AlbumDto(),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun playAudio(track: TracksDtoItem) {
        _state.value =
            state.value.copy(
                playingId = if (audioExoPlayer.playbackState.value?.isPlaying == true && curPlaying != null && curPlaying?.mediaId == track.id.toString())
                    -1
                else track.id?.toInt() ?: -1
            )
        curPlaying = track.toSong()

        curPlaying?.let {
            audioExoPlayer.playOrToggleSong(it, true)
        }
    }

    fun subscribeToObservers() {

        audioExoPlayer.mediaItems.value?.data.let { songs ->
            if (curPlaying == null && !songs.isNullOrEmpty()) {
                curPlaying = songs[0]
            }
        }

        if (audioExoPlayer.curPlayingSong.value != null) {
            curPlaying = audioExoPlayer.curPlayingSong.value?.toSong()
        }

        if (audioExoPlayer.playbackState.value != null && audioExoPlayer.playbackState.value?.isPlaying == true) {
            _state.value = state.value.copy(playingId = curPlaying?.mediaId?.toInt() ?: -1)
        } else {
            _state.value = state.value.copy(playingId = -1)

        }

    }

    fun showMore() {
        if (state.value.showCount >= (state.value.albums.data?.size ?: 0)) {
            _state.value = state.value.copy(showCount = 5)
        } else {
            _state.value = state.value.copy(showCount = state.value.showCount + 5)
        }
    }

    fun closeMiniPlayer() {
        _state.value = state.value.copy(currentTrack = TracksDtoItem())
    }

    fun playVideo(track: TracksDtoItem) {
        viewModelScope.launch {
            if (!state.value.currentTrack.id.isNullOrEmpty()) {
                _state.value = state.value.copy(currentTrack = TracksDtoItem())
                delay(100)
            }
            _state.value = state.value.copy(currentTrack = track)
        }
    }
}