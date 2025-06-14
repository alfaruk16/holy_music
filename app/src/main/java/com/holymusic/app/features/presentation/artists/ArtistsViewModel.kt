package com.holymusic.app.features.presentation.artists

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.holymusic.app.core.exoplayer.viewmodels.AudioExoPlayer
import com.holymusic.app.core.util.Async
import com.holymusic.app.features.data.remote.model.ArtistDto
import com.holymusic.app.features.domain.use_case.GetArtistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ArtistsViewModel @Inject constructor(
    val audioExoPlayer: AudioExoPlayer,
    private val getArtistUseCase: GetArtistUseCase
) :
    ViewModel() {

    private val _state = MutableStateFlow(ArtistsState())
    val state: StateFlow<ArtistsState> = _state.asStateFlow()

    init {
        getArtist()
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

    suspend fun scrollToItem(index: Int, scrollState: LazyListState) {
        scrollState.animateScrollToItem(index)
    }
}