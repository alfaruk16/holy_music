package com.holymusic.app.features.presentation.artist_audio_player

import android.app.Activity
import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.holymusic.app.core.exoplayer.isPlaying
import com.holymusic.app.MainActivity
import com.holymusic.app.core.exoplayer.data.entities.Song
import com.holymusic.app.core.exoplayer.toSong
import com.holymusic.app.core.exoplayer.viewmodels.AudioExoPlayer
import com.holymusic.app.core.util.AppConstants
import com.holymusic.app.core.util.Async
import com.holymusic.app.features.data.local.model.FileItem
import com.holymusic.app.features.data.remote.entity.Favorite
import com.holymusic.app.features.data.remote.model.ArtistDto
import com.holymusic.app.features.data.remote.model.ArtistDtoItem
import com.holymusic.app.features.data.remote.model.TracksDto
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.domain.repository.DownloadRepo
import com.holymusic.app.features.domain.services.AndroidDownloader
import com.holymusic.app.features.domain.use_case.CancelFavoriteUseCase
import com.holymusic.app.features.domain.use_case.CheckIsFavoriteUseCase
import com.holymusic.app.features.domain.use_case.GetArtistUseCase
import com.holymusic.app.features.domain.use_case.GetTrackByIdUseCase
import com.holymusic.app.features.domain.use_case.GetTracksByArtistUseCase
import com.holymusic.app.features.domain.use_case.SetFavoriteUseCase
import com.holymusic.app.features.presentation.ScreenArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistAudioPlayerViewModel @Inject constructor(
    private val getTracksByArtistUseCase: GetTracksByArtistUseCase,
    private val getArtistUseCase: GetArtistUseCase,
    val audioExoPlayer: AudioExoPlayer,
    private val getTrackByIdUseCase: GetTrackByIdUseCase,
    private val setFavoriteUseCase: SetFavoriteUseCase,
    private val checkIsFavoriteUseCase: CheckIsFavoriteUseCase,
    private val cancelFavoriteUseCase: CancelFavoriteUseCase,
    private val trackRepo: DownloadRepo,
    savedStateHandle: SavedStateHandle, private val downloader: AndroidDownloader
) :
    ViewModel() {

    private val _state = MutableStateFlow(ArtistAudioPlayerState())
    val state: StateFlow<ArtistAudioPlayerState> = _state.asStateFlow()

    private val contentString: String = checkNotNull(savedStateHandle[ScreenArgs.CONTENT])
    private val content = Gson().fromJson(contentString, TracksDtoItem::class.java)

    private var curPlaying: Song? = null

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    init {
        _state.value = state.value.copy(track = content, currentArtistId = content.artistId ?: "")
        if (content.contentBaseUrl.isNullOrEmpty() || content.artistId == null) {
            getTrackById(content.id ?: "")
        } else {
            if (audioExoPlayer.playbackState.value?.isPlaying != true && MainActivity.isPremium.value) {
                playAudio(content)
            }
        }
        if (content.artistId != null) {
            getTracksByArtist(artistId = content.artistId)
            checkIsDownloaded(content.id)
        }
        getArtist()
        subscribeToObservers()
        isFavorite(content.id)
    }

    fun checkIsDownloaded(id: String?) {
        viewModelScope.launch {
            val isDownloaded = trackRepo.isDownloaded(id ?: "")
            _state.value = state.value.copy(
                isDownloaded = isDownloaded,
                downloadProgress = if (isDownloaded) 100 else 0
            )
        }
    }

    private fun getTrackById(id: String) {

        getTrackByIdUseCase(id).onEach { result ->
            when (result) {
                is Async.Success -> {

                    _state.value = state.value.copy(
                        track = result.data?.data ?: TracksDtoItem(),
                        isLoading = false
                    )

                    checkIsDownloaded(id)
                    if (audioExoPlayer.playbackState.value?.isPlaying != true && MainActivity.isPremium.value) {
                        playAudio(state.value.track)
                    }

                    if (content.artistId == null) {
                        getTracksByArtist(artistId = state.value.track.artistId ?: "")
                    }
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

                is Async.Loading -> {
                    _state.value = state.value.copy(isLoading = true)
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
        viewModelScope.launch {
            if (state.value.track.id != track.id) {
                isFavorite(track.id)
            }
            _state.value =
                state.value.copy(
                    playingId = if (audioExoPlayer.playbackState.value?.isPlaying == true && curPlaying != null && curPlaying?.mediaId == track.id.toString())
                        -1
                    else track.id?.toInt() ?: -1,
                    track = if (track.contentBaseUrl != null) track else state.value.track
                )
            checkIsDownloaded(track.id)
            curPlaying = track.toSong()

            curPlaying?.let {
                audioExoPlayer.playOrToggleSong(it, true)
            }
        }
    }


    private fun subscribeToObservers() {

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
        }

    }

    fun showMore() {
        if (state.value.showCount >= (state.value.tracks.data?.size ?: 0)) {
            _state.value = state.value.copy(showCount = 5)
        } else {
            _state.value = state.value.copy(showCount = state.value.showCount + 5)
        }
    }

    fun artistSelected(artistDtoItem: ArtistDtoItem) {
        _state.value = state.value.copy(currentArtistId = artistDtoItem.id ?: "")
        getTracksByArtist(artistDtoItem.id.toString())
    }

    fun updateSelection(track: TracksDtoItem) {
        if (track.id != state.value.track.id) {
            isFavorite(track.id)
        }
        if (track.contentBaseUrl != null) {
            _state.value = state.value.copy(track = track)
        } else if (track.id != state.value.track.id) {
            getTrackById(track.id ?: "")
        }

        checkIsDownloaded(track.id ?: "")
    }

    suspend fun scrollToTop(index: Int, scroll: LazyListState) {
        scroll.animateScrollToItem(index)
    }

    fun setFavorite(track: TracksDtoItem) {
        if (!state.value.favoriteLoading) {
            if (!state.value.isFavorite) {
                addFavorite(track)
            } else {
                cancelFavorite(track)
            }
        }
    }

    private fun addFavorite(track: TracksDtoItem) {
        setFavoriteUseCase.invoke(Favorite(track.id ?: "", track.artistId ?: ""))
            .onEach { result ->
                when (result) {
                    is Async.Success -> {
                        _state.value =
                            state.value.copy(isFavorite = true, favoriteLoading = false)
                        updateBottomPlayerFavorite(true, track.id ?: "")
                    }

                    is Async.Loading -> _state.value = state.value.copy(favoriteLoading = true)

                    else -> {
                        _state.value = state.value.copy(favoriteLoading = false)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun cancelFavorite(track: TracksDtoItem) {
        cancelFavoriteUseCase.invoke(track.id ?: "")
            .onEach { result ->
                when (result) {
                    is Async.Success -> {
                        _state.value =
                            state.value.copy(isFavorite = false, favoriteLoading = false)
                        updateBottomPlayerFavorite(false, track.id ?: "")
                    }

                    is Async.Loading -> _state.value = state.value.copy(favoriteLoading = true)

                    else -> {
                        _state.value = state.value.copy(favoriteLoading = false)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun isFavorite(id: String?) {
        checkIsFavoriteUseCase.invoke(id ?: "").onEach { result ->
            when (result) {
                is Async.Success -> {
                    if (result.data?.data == true) {
                        _state.value = state.value.copy(isFavorite = true, favoriteLoading = false)
                    } else {
                        _state.value = state.value.copy(isFavorite = false, favoriteLoading = false)
                    }
                }

                is Async.Loading -> _state.value = state.value.copy(favoriteLoading = true)

                else -> {
                    _state.value = state.value.copy(isFavorite = false, favoriteLoading = false)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updateFavorite(favorite: Boolean, trackId: String) {
        if (state.value.track.id == trackId) {
            _state.value = state.value.copy(isFavorite = favorite)
        }
    }

    private fun updateBottomPlayerFavorite(favorite: Boolean, trackId: String) {
        if (audioExoPlayer.curPlayingSong.value?.toSong()?.mediaId == trackId) {
            audioExoPlayer.updateFavorite(favorite)
        }
    }

    fun download(activity: Activity, file: FileItem) {
        serviceScope.launch {
            if (state.value.downloadProgress == 0) {
                downloader.downloadFile(
                    activity, file, progress = {
                        _state.value = state.value.copy(downloadProgress = it)
                    })
            }
        }
    }
}