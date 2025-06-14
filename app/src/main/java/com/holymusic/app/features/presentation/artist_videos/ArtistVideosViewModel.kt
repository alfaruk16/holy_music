package com.holymusic.app.features.presentation.artist_videos

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.holymusic.app.core.exoplayer.isPlaying
import com.holymusic.app.core.exoplayer.data.entities.Song
import com.holymusic.app.core.exoplayer.toSong
import com.holymusic.app.core.exoplayer.viewmodels.AudioExoPlayer
import com.holymusic.app.core.util.AppConstants
import com.holymusic.app.core.util.Async
import com.holymusic.app.features.data.remote.model.ArtistDto
import com.holymusic.app.features.data.remote.model.ArtistDtoItem
import com.holymusic.app.features.data.remote.model.TracksDto
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.domain.use_case.GetArtistByIdUseCase
import com.holymusic.app.features.domain.use_case.GetArtistUseCase
import com.holymusic.app.features.domain.use_case.GetTracksByArtistUseCase
import com.holymusic.app.features.presentation.ScreenArgs
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
class ArtistVideosViewModel @Inject constructor(
    val audioExoPlayer: AudioExoPlayer,
    private val getTracksByArtistUseCase: GetTracksByArtistUseCase,
    private val getArtistUseCase: GetArtistUseCase,
    private val getArtistByIdUseCase: GetArtistByIdUseCase,
    savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    private val _state = MutableStateFlow(ArtistVideosState())
    val state: StateFlow<ArtistVideosState> = _state.asStateFlow()

    private val content: String = checkNotNull(savedStateHandle[ScreenArgs.CONTENT])
    private val artist = Gson().fromJson(content, ArtistDtoItem::class.java)

    private var curPlaying: Song? = null

    init {
        _state.value = state.value.copy(currentArtistId = artist.id ?: "", currentArtist = artist)
        if (artist.contentBaseUrl.isNullOrEmpty()) {
            getArtistById(artist.id)
        }
        getTracksByArtist(artistId = artist.id.toString())
        getVideoTracksByArtist(artistId = artist.id.toString())
        getArtist()
        subscribeToObservers()
    }

    private fun getArtistById(id: String?) {
        getArtistByIdUseCase.invoke(id ?: "").onEach { result ->
            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(
                        currentArtist = result.data?.data ?: state.value.currentArtist
                    )
                }

                else -> {}
            }

        }.launchIn(viewModelScope)
    }

    private fun getTracksByArtist(artistId: String) {
        getTracksByArtistUseCase(
            artistId = artistId,
            type = AppConstants.typeAudio
        ).onEach { result ->

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

    private fun getVideoTracksByArtist(artistId: String) {
        getTracksByArtistUseCase(
            artistId = artistId,
            type = AppConstants.typeVideo
        ).onEach { result ->

            when (result) {
                is Async.Success -> {

                    _state.value = state.value.copy(
                        videoTracks = result.data ?: TracksDto(),
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
                        artistList = result.data ?: ArtistDto(),
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

    fun artistSelected(artistDtoItem: ArtistDtoItem) {
        _state.value = state.value.copy(
            currentArtistId = artistDtoItem.id ?: "",
            currentArtist = artistDtoItem
        )
        getTracksByArtist(artistDtoItem.id.toString())
    }

    suspend fun scrollToItem(index: Int, scrollState: LazyListState) {
        scrollState.animateScrollToItem(index)
    }

    fun tabChanged(index: Int) {
        _state.value = state.value.copy(selectedTab = index)
    }

    fun closeMiniPlayer() {
        _state.value = state.value.copy(currentTrack = TracksDtoItem())
    }

    fun playVideo(it: TracksDtoItem) {
        viewModelScope.launch {
            if (!state.value.currentTrack.id.isNullOrEmpty()) {
                _state.value = state.value.copy(currentTrack = TracksDtoItem())
                delay(100)
            }
            _state.value = state.value.copy(currentTrack = it)
        }
    }
}