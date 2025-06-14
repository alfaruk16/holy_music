package com.holymusic.app.features.presentation.album_audios

import androidx.compose.foundation.lazy.grid.LazyGridState
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
import com.holymusic.app.features.data.remote.model.AlbumDto
import com.holymusic.app.features.data.remote.model.AlbumDtoItem
import com.holymusic.app.features.data.remote.model.AlbumTrackDto
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.domain.use_case.GetAlbumByIdUseCase
import com.holymusic.app.features.domain.use_case.GetAlbumByTypeUseCase
import com.holymusic.app.features.domain.use_case.GetAlbumTrackUseCase
import com.holymusic.app.features.presentation.ScreenArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AlbumAudiosViewModel @Inject constructor(
    val audioExoPlayer: AudioExoPlayer,
    private val getAlbumByTypeUseCase: GetAlbumByTypeUseCase,
    private val getAlbumTrackUseCase: GetAlbumTrackUseCase,
    private val getAlbumByIdUseCase: GetAlbumByIdUseCase,
    savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    private val _state = MutableStateFlow(AlbumAudiosState())
    val state: StateFlow<AlbumAudiosState> = _state.asStateFlow()

    private val content: String = checkNotNull(savedStateHandle[ScreenArgs.CONTENT])
    private val album = Gson().fromJson(content, AlbumDtoItem::class.java)

    private var curPlaying: Song? = null

    init {
        _state.value = state.value.copy(currentAlbumId = album.id ?: "", currentAlbum = album)
        if (album.contentBaseUrl.isNullOrEmpty()) {
            getAlbumById(album.id)
        }
        getTracksByAlbum(albumId = album.id ?: "")
        getAlbum()
        subscribeToObservers()
    }

    private fun getAlbumById(id: String?) {
        getAlbumByIdUseCase.invoke(id ?: "").onEach { result ->
            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(
                        currentAlbum = result.data?.data ?: state.value.currentAlbum
                    )
                }

                else -> {}
            }

        }.launchIn(viewModelScope)
    }

    private fun getTracksByAlbum(albumId: String) {
        getAlbumTrackUseCase(
            id = albumId
        ).onEach { result ->

            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(
                        tracks = result.data ?: AlbumTrackDto(),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun getAlbum() {
        getAlbumByTypeUseCase(AppConstants.typeAudio).onEach { result ->
            when (result) {
                is Async.Success -> {

                    _state.value = state.value.copy(
                        albumList = result.data ?: AlbumDto(),
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
        if (state.value.showCount >= (state.value.tracks.data?.size ?: 0)) {
            _state.value = state.value.copy(showCount = 5)
        } else {
            _state.value = state.value.copy(showCount = state.value.showCount + 5)
        }
    }

    fun albumSelected(album: AlbumDtoItem) {
        _state.value = state.value.copy(currentAlbumId = album.id ?: "", currentAlbum = album)
        getTracksByAlbum(album.id ?: "")
    }

    suspend fun scrollToTop(index: Int, scroll: LazyGridState) {
        scroll.animateScrollToItem(index)
    }
}