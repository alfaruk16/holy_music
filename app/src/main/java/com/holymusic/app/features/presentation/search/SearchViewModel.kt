package com.holymusic.app.features.presentation.search

import androidx.compose.ui.focus.FocusManager
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
import com.holymusic.app.features.domain.use_case.GetArtistUseCase
import com.holymusic.app.features.domain.use_case.GetTracksByTypeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getArtistUseCase: GetArtistUseCase,
    val audioExoPlayer: AudioExoPlayer,
    private val getTracksUseCase: GetTracksByTypeUseCase
) :
    ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private var curPlaying: Song? = null

    fun init() {
        getArtist()
        getTrack()
        getVideoTrack()
        subscribeToObservers()
    }

    private fun getTrack() {
        getTracksUseCase(type = AppConstants.typeAudio).onEach { result ->
            when (result) {
                is Async.Success -> {

                    _state.value = state.value.copy(
                        tracks = result.data ?: TracksDto(),
                        searchedTracks = result.data ?: TracksDto(),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun getVideoTrack() {
        getTracksUseCase(type = AppConstants.typeVideo).onEach { result ->
            when (result) {
                is Async.Success -> {

                    _state.value = state.value.copy(
                        videoTracks = result.data ?: TracksDto(),
                        searchedVideoTracks = result.data ?: TracksDto(),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun getArtist() {
        getArtistUseCase().onEach { result ->
            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(
                        artist = result.data ?: ArtistDto(),
                        searchedArtist = result.data ?: ArtistDto(),
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
        if (state.value.selectedTab == 0) {
            if (state.value.showCount >= (state.value.searchedTracks.data?.size ?: 0)) {
                _state.value = state.value.copy(showCount = 5)
            } else {
                _state.value = state.value.copy(showCount = state.value.showCount + 5)
            }
        } else {
            if (state.value.showCountVideo >= (state.value.searchedVideoTracks.data?.size ?: 0)) {
                _state.value = state.value.copy(showCountVideo = 5)
            } else {
                _state.value = state.value.copy(showCountVideo = state.value.showCountVideo + 5)
            }
        }
    }

    fun searchChanged(text: String, focusManager: FocusManager) {
        viewModelScope.launch {
            if (text.isNotEmpty()) {
                val tracks = state.value.tracks.data?.filter {
                    (it.title?.lowercase(Locale.ROOT)
                        ?.contains(text) == true || it.artistName?.lowercase(Locale.ROOT)
                        ?.contains(text) == true)
                }
                val videoTracks = state.value.videoTracks.data?.filter {
                    (it.title?.lowercase(Locale.ROOT)
                        ?.contains(text) == true || it.artistName?.lowercase(Locale.ROOT)
                        ?.contains(text) == true)
                }
                val artists = state.value.artist.data?.filter {
                    it.name?.lowercase(Locale.ROOT)?.contains(text) ?: false
                }
                _state.value =
                    state.value.copy(
                        search = text,
                        searchedTracks = state.value.searchedTracks.copy(data = tracks),
                        searchedVideoTracks = state.value.searchedVideoTracks.copy(data = videoTracks),
                        searchedArtist = state.value.searchedArtist.copy(data = artists)
                    )
            } else {
                _state.value =
                    state.value.copy(
                        search = text,
                        searchedTracks = state.value.tracks,
                        searchedVideoTracks = state.value.videoTracks,
                        searchedArtist = state.value.artist
                    )
                focusManager.clearFocus()
            }
        }
    }

    fun tabChanged(index: Int) {
        _state.value = state.value.copy(selectedTab = index)
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