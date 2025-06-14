package com.holymusic.app.features.presentation.video_artists

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
import com.holymusic.app.features.data.remote.model.CategoryDtoItem
import com.holymusic.app.features.data.remote.model.TrackBillboardDto
import com.holymusic.app.features.data.remote.model.TracksDto
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.domain.use_case.GetArtistUseCase
import com.holymusic.app.features.domain.use_case.GetTrackBillboardUseCase
import com.holymusic.app.features.domain.use_case.GetTracksByTypeUseCase
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
import com.holymusic.app.R

@HiltViewModel
class VideoArtistViewModel @Inject constructor(
    private val getArtistUseCase: GetArtistUseCase,
    val audioExoPlayer: AudioExoPlayer,
    private val getTracksUseCase: GetTracksByTypeUseCase,
    private val getTrackBillboardUseCase: GetTrackBillboardUseCase,
    savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    private val _state = MutableStateFlow(VideoArtistState())
    val state: StateFlow<VideoArtistState> = _state.asStateFlow()

    private val categoryString: String = checkNotNull(savedStateHandle[ScreenArgs.CATEGORY] ?: "")
    private val category = try {
        Gson().fromJson(categoryString, CategoryDtoItem::class.java)
    }catch (_: Exception){
        CategoryDtoItem(name = AppConstants.gajalVideo, icon = R.drawable.video)
    }

    private var curPlaying: Song? = null

    fun init() {
        getArtist()
        getTrack(state.value.page, state.value.showCount)
        getTrackBillboard()
    }

    private fun getTrackBillboard() {

        getTrackBillboardUseCase().onEach { result ->
            when (result) {
                is Async.Success -> {

                    val list =
                        result.data?.data?.filter { it.contentType == AppConstants.trackType && it.contentCategory == AppConstants.typeVideo }

                    _state.value = state.value.copy(
                        trackBillboard = result.data?.copy(data = list) ?: TrackBillboardDto(),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun getTrack(page: Int, showCount: Int) {
        getTracksUseCase(
            type = AppConstants.typeVideo,
            take = state.value.take,
            page = page
        ).onEach { result ->
            when (result) {
                is Async.Success -> {

                    var data = result.data?.data
                    if (category != null && category.isPopular) {
                        data = data?.sortedByDescending { it.playCount }
                    }

                    if (category != null && category.isFavorite) {
                        data = data?.sortedByDescending { it.totalFav }
                    }

                    val all = (state.value.tracks.data?.toMutableList()
                        ?: emptyList<TracksDtoItem>().toMutableList()).apply {
                        if (state.value.tracks.data?.size != state.value.page * state.value.take)
                            addAll(
                                data ?: emptyList()
                            )
                    }

                    _state.value = state.value.copy(
                        tracks = result.data?.copy(data = all) ?: TracksDto(),
                        isLoading = false,
                        page = page,
                        showCount = showCount + 5
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

    fun showMore() {
        if (state.value.showCount == state.value.tracks.data?.size && (state.value.tracks.data?.size
                ?: 0) < (state.value.tracks.totalRecords ?: 0)
        ) {
            getTrack(state.value.page + 1, state.value.showCount)
        } else if (state.value.showCount >= (state.value.tracks.totalRecords ?: 0)) {
            _state.value = state.value.copy(showCount = 5)
        } else if (state.value.tracks.data?.size == state.value.page * state.value.take) {
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