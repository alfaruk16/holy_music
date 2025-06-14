package com.holymusic.app.features.presentation.album_video_player

import android.app.Activity
import android.app.Application
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
import com.holymusic.app.features.data.remote.model.AlbumDto
import com.holymusic.app.features.data.remote.model.AlbumDtoItem
import com.holymusic.app.features.data.remote.model.AlbumTrackDto
import com.holymusic.app.features.data.remote.model.AlbumTrackDtoItem
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.domain.repository.DownloadRepo
import com.holymusic.app.features.domain.services.AndroidDownloader
import com.holymusic.app.features.domain.use_case.AddPlayCountUseCase
import com.holymusic.app.features.domain.use_case.CancelFavoriteUseCase
import com.holymusic.app.features.domain.use_case.CheckIsFavoriteUseCase
import com.holymusic.app.features.domain.use_case.GetAlbumByTypeUseCase
import com.holymusic.app.features.domain.use_case.GetAlbumTrackUseCase
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
class AlbumVideoPlayerViewModel @Inject constructor(
    private val getAlbumTrackDto: GetAlbumTrackUseCase,
    private val getAlbumByTypeUseCase: GetAlbumByTypeUseCase,
    val audioExoPlayer: AudioExoPlayer,
    private val addPlayCountUseCase: AddPlayCountUseCase,
    private val setFavoriteUseCase: SetFavoriteUseCase,
    private val checkIsFavoriteUseCase: CheckIsFavoriteUseCase,
    private val cancelFavoriteUseCase: CancelFavoriteUseCase,
    savedStateHandle: SavedStateHandle,
    private val trackRepo: DownloadRepo, application: Application,
    private val downloader: AndroidDownloader
) :
    ViewModel() {

    private val _state = MutableStateFlow(AlbumVideoPlayerState())
    val state: StateFlow<AlbumVideoPlayerState> = _state.asStateFlow()

    private val contentString: String = checkNotNull(savedStateHandle[ScreenArgs.CONTENT])
    private val content = Gson().fromJson(contentString, AlbumTrackDtoItem::class.java)

    private var curPlaying: Song? = null

    private val serviceScope = CoroutineScope(Dispatchers.Main)

    init {
        _state.value = state.value.copy(track = content, currentArtistId = content.albumId ?: "")
        getAlbumTracks(id = content.albumId.toString())
        checkIsDownloaded(content.trackId ?: "")
        getAlbum()
        isFavorite(content.trackId)
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
                            TrackId = it.trackId ?: ""
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


    private fun getAlbumTracks(id: String) {
        getAlbumTrackDto(
            id = id,
        ).onEach { result ->

            when (result) {
                is Async.Success -> {

                    _state.value = state.value.copy(
                        tracks = result.data ?: AlbumTrackDto(),
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

    private fun getAlbum() {
        getAlbumByTypeUseCase(AppConstants.typeVideo).onEach { result ->
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

    fun showMore() {
        if (state.value.showCount >= (state.value.tracks.data?.size ?: 0)) {
            _state.value = state.value.copy(showCount = 5)
        } else {
            _state.value = state.value.copy(showCount = state.value.showCount + 5)
        }
    }

    fun albumSelected(album: AlbumDtoItem) {
        _state.value = state.value.copy(currentArtistId = album.id ?: "")
        getAlbumTracks(album.id.toString())
    }

//    fun updateSelection(track: AlbumTrackDtoItem) {
//        _state.value = state.value.copy(track = track)
//    }

    suspend fun scrollToItem(index: Int, scrollState: LazyListState) {
        scrollState.animateScrollToItem(index)
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