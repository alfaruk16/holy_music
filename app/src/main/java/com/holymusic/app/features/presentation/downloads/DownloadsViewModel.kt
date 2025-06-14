package com.holymusic.app.features.presentation.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.holymusic.app.core.exoplayer.viewmodels.AudioExoPlayer
import com.holymusic.app.core.util.AppConstants
import com.holymusic.app.core.util.AudioPlayer
import com.holymusic.app.features.data.local.toTrackDtoItem
import com.holymusic.app.features.data.remote.model.TracksDto
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.domain.repository.DownloadRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DownloadsViewModel @Inject constructor(
    private val trackRepo: DownloadRepo,
    val audioExoPlayer: AudioExoPlayer,
    private val audioPlayer: AudioPlayer
) :
    ViewModel() {

    private val _state = MutableStateFlow(DownloadsState())
    val state: StateFlow<DownloadsState> = _state.asStateFlow()

    fun getTracks() {
        viewModelScope.launch {
            val tracks = trackRepo.getTracks().map { track ->
                track.toTrackDtoItem()
            }

            val audios = tracks.filter { it.contentCategory == AppConstants.typeAudio }
            val videos = tracks.filter { it.contentCategory == AppConstants.typeVideo }

            _state.value = state.value.copy(
                tracks = TracksDto(data = audios.reversed(), status = 200),
                videoTracks = TracksDto(data = videos.reversed(), status = 200)
            )
        }
    }

    fun playAudio(track: TracksDtoItem) {
        val file = File(track.streamUrl ?: "")
        if (file.exists()) {
            audioExoPlayer.pause()
            _state.value = state.value.copy(playingId = track.id ?: "")
            audioPlayer.play(track.streamUrl ?: "", onFinished = {
                _state.value = state.value.copy(playingId = it)
            })
        } else {
            _state.value = state.value.copy(message = "File not found", showSnackBar = true)
        }
    }

    fun pause() {
        audioPlayer.pause()
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

    fun closeSnackBar() {
        _state.value = state.value.copy(showSnackBar = false)
    }

    fun delete(track: TracksDtoItem) {
        viewModelScope.launch {
            trackRepo.deleteTrack(track.id ?: "")
            getTracks()
        }
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