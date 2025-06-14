package com.holymusic.app.features.presentation.artist_video_player

import android.app.Activity
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
import com.holymusic.app.features.data.local.model.FileItem
import com.holymusic.app.features.data.remote.entity.Favorite
import com.holymusic.app.features.data.remote.entity.PlayCount
import com.holymusic.app.features.data.remote.model.ArtistDto
import com.holymusic.app.features.data.remote.model.ArtistDtoItem
import com.holymusic.app.features.data.remote.model.TracksDto
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.domain.repository.DownloadRepo
import com.holymusic.app.features.domain.services.AndroidDownloader
import com.holymusic.app.features.domain.use_case.AddPlayCountUseCase
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class ArtistVideoPlayerViewModel @Inject constructor(
    private val getTracksByArtistUseCase: GetTracksByArtistUseCase,
    private val getArtistUseCase: GetArtistUseCase,
    private val getTrackByIdUseCase: GetTrackByIdUseCase,
    audioExoPlayer: AudioExoPlayer,
    private val addPlayCountUseCase: AddPlayCountUseCase,
    private val setFavoriteUseCase: SetFavoriteUseCase,
    private val checkIsFavoriteUseCase: CheckIsFavoriteUseCase,
    private val cancelFavoriteUseCase: CancelFavoriteUseCase,
    private val trackRepo: DownloadRepo,
    savedStateHandle: SavedStateHandle, private val downloader: AndroidDownloader
) :
    ViewModel() {

    private val _state = MutableStateFlow(ArtistVideoPlayerState())
    val state: StateFlow<ArtistVideoPlayerState> = _state.asStateFlow()

    private val content: String = checkNotNull(savedStateHandle[ScreenArgs.CONTENT])
    private val track = Gson().fromJson(content, TracksDtoItem::class.java)

    private val serviceScope = CoroutineScope(Dispatchers.Main)

    init {
        _state.value = ArtistVideoPlayerState(track = track)
        if (!track.contentBaseUrl.isNullOrEmpty() && track.artistId.isNullOrEmpty()) {
            getTrack(track.id ?: "")
        } else if (!track.contentBaseUrl.isNullOrEmpty()) {
            getTracksByArtist(track.artistId ?: "")
            checkIsDownloaded(track.id ?: "")
        }
        if (!track.contentBaseUrl.isNullOrEmpty()) {
            getArtist()
            isFavorite(track.id)
        }
        if(track.streamUrl.isNullOrEmpty()){
            getTrack(track.id ?: "")
            getArtist()
            isFavorite(track.id)
        }
        if (audioExoPlayer.playbackState.value != null && audioExoPlayer.playbackState.value?.isPlaying == true) {
            audioExoPlayer.playOrToggleSong(
                audioExoPlayer.curPlayingSong.value?.toSong() ?: Song(),
                true
            )
        }
    }

    private fun checkIsDownloaded(id: String?) {
        viewModelScope.launch {
            val isDownloaded = trackRepo.isDownloaded(id ?: "")
            _state.value = state.value.copy(
                isDownloaded = isDownloaded,
                downloadProgress = if (isDownloaded) 100 else 0
            )
        }
    }

    fun addPlayCount(curPlayerPosition: Long) {
        if (curPlayerPosition / 1000 >= 15) {

            val requestBody: RequestBody = Gson().toJson(
                state.value.track
                    .let {
                        PlayCount(
                            AppLanguage = AppConstants.language,
                            ArtistId = it.artistId ?: "",
                            PlayInSecond = (curPlayerPosition.div(1000)).toInt().toString(),
                            StreamCount = "1",
                            TrackId = it.id ?: ""
                        )
                    }
            )
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())

            addPlayCountUseCase(requestBody).onEach { result ->

                when (result) {
                    is Async.Success -> {
                        println("Success")
                    }

                    else -> {}
                }
            }.launchIn(serviceScope)
        }
    }

    private fun getTrack(id: String) {
        println("Called")

        getTrackByIdUseCase(id).onEach { result ->
            when (result) {
                is Async.Success -> {

                    _state.value = state.value.copy(
                        track = result.data?.data ?: TracksDtoItem(),
                        isLoading = false
                    )

                    checkIsDownloaded(id)

                    getTracksByArtist(result.data?.data?.artistId ?: "")
                }

                is Async.Loading -> _state.value = state.value.copy(isLoading = true)

                else -> _state.value = state.value.copy(isLoading = false)
            }
        }.launchIn(viewModelScope)
    }

    private fun getTracksByArtist(artistId: String) {
        getTracksByArtistUseCase(
            artistId = artistId,
            type = AppConstants.typeVideo
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

//    fun updateSelection(track: TracksDtoItem) {
//        _state.value = state.value.copy(track = track)
//    }

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
                        _state.value = state.value.copy(isFavorite = true)
                    }
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun download(activity: Activity, file: FileItem) {
        serviceScope.launch {
            if (state.value.downloadProgress == 0) {
                downloader.downloadFile(activity, file, progress = {
                    _state.value = state.value.copy(downloadProgress = it)
                })
            }
        }
    }
}