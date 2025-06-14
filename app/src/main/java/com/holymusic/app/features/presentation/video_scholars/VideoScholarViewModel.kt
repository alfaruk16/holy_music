package com.holymusic.app.features.presentation.video_scholars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.holymusic.app.core.exoplayer.isPlaying
import com.holymusic.app.core.exoplayer.data.entities.Song
import com.holymusic.app.core.exoplayer.toSong
import com.holymusic.app.core.exoplayer.viewmodels.AudioExoPlayer
import com.holymusic.app.core.util.AppConstants
import com.holymusic.app.core.util.Async
import com.holymusic.app.features.data.remote.model.ArtistDto
import com.holymusic.app.features.data.remote.model.TracksDto
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.domain.use_case.GetScholarTrackByType
import com.holymusic.app.features.domain.use_case.GetScholarUseCase
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
class VideoScholarViewModel @Inject constructor(
    private val getScholarUseCase: GetScholarUseCase,
    val audioExoPlayer: AudioExoPlayer,
    private val getScholarTrackByType: GetScholarTrackByType
) :
    ViewModel() {

    private val _state = MutableStateFlow(VideoScholarState())
    val state: StateFlow<VideoScholarState> = _state.asStateFlow()

    private var curPlaying: Song? = null

    init {
        getArtist()
        getTrack()
        subscribeToObservers()
    }

    private fun getTrack() {
        getScholarTrackByType(type = AppConstants.typeVideo).onEach { result ->
            when (result) {
                is Async.Success -> {

                    _state.value = state.value.copy(
                        tracks = result.data ?: TracksDto(),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun getArtist() {
        getScholarUseCase().onEach { result ->
            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(
                        artist = result.data ?: ArtistDto(),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun playAudio(track: TracksDtoItem) {
        audioExoPlayer.playOrToggleSong(track.toSong(), true)
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
            _state.value = state.value.copy(playingId = curPlaying?.mediaId ?: "")
        } else {
            _state.value = state.value.copy(playingId = "")
        }
    }

    fun showMore() {
        if (state.value.showCount >= (state.value.tracks.data?.size ?: 0)) {
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