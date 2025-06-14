package com.holymusic.app.features.presentation.my_favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.holymusic.app.core.exoplayer.isPlaying
import com.holymusic.app.core.exoplayer.data.entities.Song
import com.holymusic.app.core.exoplayer.toSong
import com.holymusic.app.core.exoplayer.viewmodels.AudioExoPlayer
import com.holymusic.app.core.util.AppConstants
import com.holymusic.app.core.util.Async
import com.holymusic.app.features.data.remote.model.TracksDto
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.domain.use_case.GetMyFavoritesUseCase
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
class MyFavoritesViewModel @Inject constructor(
    val audioExoPlayer: AudioExoPlayer,
    private val getMyFavoritesUseCase: GetMyFavoritesUseCase
) :
    ViewModel() {

    private val _state = MutableStateFlow(MyFavoritesState())
    val state: StateFlow<MyFavoritesState> = _state.asStateFlow()

    private var curPlaying: Song? = null

    fun getTracks() {
        getMyFavoritesUseCase().onEach { result ->

            when (result) {
                is Async.Success -> {

                    val list = result.data?.data
                    val audios = list?.filter { it.contentCategory == AppConstants.typeAudio }
                    val videos = list?.filter { it.contentCategory == AppConstants.typeVideo }

                    _state.value = state.value.copy(
                        tracks = result.data?.copy(data = audios) ?: TracksDto(),
                        videoTracks = result.data?.copy(data = videos) ?: TracksDto(),
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
                playingId = if (audioExoPlayer.playbackState.value?.isPlaying == true && curPlaying != null && curPlaying?.mediaId == track.id)
                    -1
                else track.id?.toInt() ?: 0
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
            _state.value = state.value.copy(playingId = curPlaying?.mediaId?.toInt() ?: 0)
        } else {
            _state.value = state.value.copy(playingId = -1)

        }

    }

    fun showMore() {
        if (state.value.selectedTab == 0) {
            if (state.value.showCount >= (state.value.tracks.data?.size ?: 0)) {
                _state.value = state.value.copy(showCount = 5)
            } else {
                _state.value = state.value.copy(showCount = state.value.showCount + 5)
            }
        } else {
            if (state.value.showCountVideo >= (state.value.videoTracks.data?.size ?: 0)) {
                _state.value = state.value.copy(showCountVideo = 5)
            } else {
                _state.value = state.value.copy(showCountVideo = state.value.showCountVideo + 5)
            }
        }
    }

    fun tabChanged(index: Int) {
        _state.value = state.value.copy(selectedTab = index)
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

    fun closeMiniPlayer() {
        _state.value = state.value.copy(currentTrack = TracksDtoItem())
    }
}