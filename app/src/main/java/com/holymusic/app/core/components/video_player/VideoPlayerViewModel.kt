package com.holymusic.app.core.components.video_player

import android.app.Activity
import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.holymusic.app.core.util.AppConstants
import com.holymusic.app.core.util.Async
import com.holymusic.app.core.util.Enums
import com.holymusic.app.features.data.local.model.FileItem
import com.holymusic.app.features.data.local.toTrackDtoItem
import com.holymusic.app.features.data.remote.entity.Favorite
import com.holymusic.app.features.data.remote.entity.PlayCount
import com.holymusic.app.features.data.remote.model.AlbumDto
import com.holymusic.app.features.data.remote.model.AlbumDtoItem
import com.holymusic.app.features.data.remote.model.AlbumTrackDto
import com.holymusic.app.features.data.remote.model.ArtistDto
import com.holymusic.app.features.data.remote.model.TracksDto
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.data.remote.model.toTrackDtoItem
import com.holymusic.app.features.domain.repository.DownloadRepo
import com.holymusic.app.features.domain.services.AndroidDownloader
import com.holymusic.app.features.domain.use_case.AddPlayCountUseCase
import com.holymusic.app.features.domain.use_case.CancelFavoriteUseCase
import com.holymusic.app.features.domain.use_case.CheckIsFavoriteUseCase
import com.holymusic.app.features.domain.use_case.GetAlbumByTypeUseCase
import com.holymusic.app.features.domain.use_case.GetAlbumTrackUseCase
import com.holymusic.app.features.domain.use_case.GetArtistUseCase
import com.holymusic.app.features.domain.use_case.GetScholarTrackByArtistUseCase
import com.holymusic.app.features.domain.use_case.GetScholarUseCase
import com.holymusic.app.features.domain.use_case.GetTrackByIdUseCase
import com.holymusic.app.features.domain.use_case.GetTracksByArtistUseCase
import com.holymusic.app.features.domain.use_case.SetFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val getTracksByArtistUseCase: GetTracksByArtistUseCase,
    private val getArtistUseCase: GetArtistUseCase,
    private val setFavoriteUseCase: SetFavoriteUseCase,
    private val cancelFavoriteUseCase: CancelFavoriteUseCase,
    private val checkIsFavoriteUseCase: CheckIsFavoriteUseCase,
    private val downloader: AndroidDownloader,
    private val addPlayCountUseCase: AddPlayCountUseCase,
    private val getTrackByIdUseCase: GetTrackByIdUseCase,
    private val getScholarUseCase: GetScholarUseCase,
    private val getScholarTrackByArtistUseCase: GetScholarTrackByArtistUseCase,
    private val getAlbumTrackUseCase: GetAlbumTrackUseCase,
    private val getAlbumByTypeUseCase: GetAlbumByTypeUseCase,
    private val downloadRepo: DownloadRepo
) : ViewModel() {

    private val _state = MutableStateFlow(VideoPlayerState())
    val state: StateFlow<VideoPlayerState> = _state.asStateFlow()
    private val serviceScope = CoroutineScope(Dispatchers.Main)

    fun init(track: TracksDtoItem, videoType: String) {
        when (videoType) {
            Enums.VideoType.Artist.name -> {
                if (!track.artistId.isNullOrEmpty()) {
                    getArtistTracks(track.artistId)
                } else {
                    getTrack(track.id ?: "", videoType)
                }
                getArtist()
            }

            Enums.VideoType.Scholar.name -> {
                if (!track.artistId.isNullOrEmpty()) {
                    getScholarTracks(track.artistId)
                } else {
                    getTrack(track.id ?: "", videoType)
                }
                getScholars()
            }

            Enums.VideoType.Album.name -> {
                getAlbumTracks(track.albumId ?: "")
                getAlbum()
            }

            Enums.VideoType.Download.name -> {
                getDownloadedTracks()
            }
        }
        checkIsDownloaded(track.id)
        isFavorite(track.id)

        playVideo(track, videoType)
    }

    fun playVideo(track: TracksDtoItem, videoType: String) {
        viewModelScope.launch {
            if (!state.value.currentTrack.id.isNullOrEmpty()) {
                _state.value = state.value.copy(currentTrack = TracksDtoItem())
                delay(100)
            }
            _state.value = state.value.copy(
                currentTrack = track,
                playingId = track.id ?: "",
                videoType = videoType
            )
        }
    }

    private fun getDownloadedTracks() {
        viewModelScope.launch {
            val tracks = downloadRepo.getTracks().map { track ->
                track.toTrackDtoItem()
            }
            val videos = tracks.filter { it.contentCategory == AppConstants.typeVideo }

            _state.value = state.value.copy(
                downloadedTracks = TracksDto(data = videos.reversed(), status = 200)
            )
        }
    }

    private fun getAlbum() {
        getAlbumByTypeUseCase(AppConstants.typeVideo).onEach { result ->
            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(
                        albums = result.data ?: AlbumDto(),
                        isLoading = false
                    )
                }

                is Async.Error -> {
                    _state.value = state.value.copy(isLoading = false)
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun getAlbumTracks(id: String) {
        getAlbumTrackUseCase(
            id = id,
        ).onEach { result ->

            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(
                        albumTracks = result.data ?: AlbumTrackDto(),
                        isLoading = false
                    )
                }

                is Async.Loading -> {
                    _state.value = state.value.copy(isLoading = true)
                }

                is Async.Error -> {
                    _state.value = state.value.copy(isLoading = false)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getScholarTracks(artistId: String) {
        getScholarTrackByArtistUseCase(
            artistId = artistId,
            type = AppConstants.typeVideo
        ).onEach { result ->

            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(
                        scholarTracks = result.data ?: TracksDto(),
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

    private fun getScholars() {
        getScholarUseCase().onEach { result ->
            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(
                        scholars = result.data ?: ArtistDto(),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun getTrack(id: String, videoType: String) {

        getTrackByIdUseCase(id).onEach { result ->
            when (result) {
                is Async.Success -> {

                    _state.value = state.value.copy(
                        currentTrack = result.data?.data ?: TracksDtoItem(),
                        isLoading = false
                    )
                    checkIsDownloaded(id)
                    if (videoType == Enums.VideoType.Artist.name) {
                        getArtistTracks(result.data?.data?.artistId ?: "")
                    } else if (videoType == Enums.VideoType.Scholar.name) {
                        getScholarTracks(result.data?.data?.artistId ?: "")
                    }
                }

                is Async.Loading -> _state.value = state.value.copy(isLoading = true)

                else -> {
                    _state.value = state.value.copy(isLoading = false)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun checkIsDownloaded(id: String?) {
        viewModelScope.launch {
            val isDownloaded = downloader.isDownloaded(id ?: "")
            _state.value = state.value.copy(
                isDownloaded = isDownloaded,
                downloadProgress = if (isDownloaded) 100 else 0
            )
        }
    }

    fun getArtistTracks(artistId: String) {
        getTracksByArtistUseCase(
            artistId = artistId,
            type = AppConstants.typeVideo
        ).onEach { result ->

            when (result) {
                is Async.Success -> {

                    _state.value = state.value.copy(
                        artistTracks = result.data ?: TracksDto(),
                        isLoading = false
                    )
                }

                is Async.Loading -> {
                    _state.value = state.value.copy(isLoading = true)
                }

                else -> {
                    _state.value = state.value.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun showMoreArtist() {
        if (state.value.showCountArtist >= (state.value.artistTracks.data?.size ?: 0)) {
            _state.value = state.value.copy(showCountArtist = 5)
        } else {
            _state.value = state.value.copy(showCountArtist = state.value.showCountArtist + 5)
        }
    }

    fun showMoreScholar() {
        if (state.value.showCountScholar >= (state.value.scholarTracks.data?.size ?: 0)) {
            _state.value = state.value.copy(showCountScholar = 5)
        } else {
            _state.value = state.value.copy(showCountScholar = state.value.showCountScholar + 5)
        }
    }

    fun showMoreKhatam() {
        if (state.value.showCountKhatam >= (state.value.khatamTracks.data?.size ?: 0)) {
            _state.value = state.value.copy(showCountKhatam = 5)
        } else {
            _state.value = state.value.copy(showCountKhatam = state.value.showCountKhatam + 5)
        }
    }

    fun showMoreAlbum() {
        if (state.value.showCountAlbum >= (state.value.albumTracks.data?.size ?: 0)) {
            _state.value = state.value.copy(showCountAlbum = 5)
        } else {
            _state.value = state.value.copy(showCountAlbum = state.value.showCountAlbum + 5)
        }
    }

    fun showMoreDownloads() {
        if (state.value.showCountdownloads >= (state.value.downloadedTracks.data?.size ?: 0)) {
            _state.value = state.value.copy(showCountdownloads = 5)
        } else {
            _state.value = state.value.copy(showCountdownloads = state.value.showCountdownloads + 5)
        }
    }

    fun getArtist() {
        getArtistUseCase().onEach { result ->
            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(
                        artist = result.data ?: ArtistDto(),
                        isLoading = false
                    )
                }

                is Async.Error -> {
                    _state.value = state.value.copy(isLoading = false)
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun closePlayer() {
        _state.value = state.value.copy(currentTrack = TracksDtoItem())
    }

    suspend fun scrollToTop(scrollState: LazyListState) {
        scrollState.animateScrollToItem(0)
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
        _state.value = state.value.copy(isFavorite = false)
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

        if (state.value.downloadProgress == 0) {
            downloader.downloadFile(
                activity, file,
                progress = {
                    _state.value = state.value.copy(downloadProgress = it)
                },
            )
        }
    }

    fun addPlayCount(curPlayerPosition: Long, track: TracksDtoItem) {
        if (curPlayerPosition / 1000 >= 15) {

            val requestBody: RequestBody = Gson().toJson(
                state.value.currentTrack.let {
                    PlayCount(
                        AppLanguage = AppConstants.language,
                        ArtistId = track.artistId ?: "",
                        PlayInSecond = (curPlayerPosition.div(1000)).toInt().toString(),
                        StreamCount = "1",
                        TrackId = track.id ?: ""
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

    fun albumSelected(album: AlbumDtoItem) {
        getAlbumTracks(album.id ?: "")
    }

    fun delete(track: TracksDtoItem) {
        viewModelScope.launch {
            downloadRepo.deleteTrack(track.id ?: "")
            getDownloadedTracks()
            if (!track.streamUrl.isNullOrEmpty()) {
                val file = File(track.streamUrl)
                if (file.exists()) {
                    file.delete()
                }
            }
        }
    }

    fun playOnFinished(): Boolean {

        if (!state.value.artistTracks.data.isNullOrEmpty()) {
            val size = state.value.artistTracks.data?.size ?: 0
            for (i in 0..<size) {
                if (state.value.artistTracks.data?.get(i)?.id == state.value.currentTrack.id && i < (size - 1)) {
                    playVideo(
                        state.value.artistTracks.data?.get(i + 1) ?: TracksDtoItem(),
                        videoType = Enums.VideoType.Artist.name
                    )
                    return true
                }
            }
        }

        if (!state.value.scholarTracks.data.isNullOrEmpty()) {
            val size = state.value.scholarTracks.data?.size ?: 0
            for (i in 0..<size) {
                if (state.value.scholarTracks.data?.get(i)?.id == state.value.currentTrack.id && i < (size - 1)) {
                    playVideo(
                        state.value.scholarTracks.data?.get(i + 1) ?: TracksDtoItem(),
                        videoType = Enums.VideoType.Scholar.name
                    )
                    return true
                }
            }
        }

        if (!state.value.albumTracks.data.isNullOrEmpty()) {
            val size = state.value.albumTracks.data?.size ?: 0
            for (i in 0..<size) {
                if (state.value.albumTracks.data?.get(i)?.id == state.value.currentTrack.id && i < (size - 1)) {
                    playVideo(
                        state.value.albumTracks.data?.get(i + 1)?.toTrackDtoItem()
                            ?: TracksDtoItem(),
                        videoType = Enums.VideoType.Album.name
                    )
                    return true
                }
            }
        }

        if (!state.value.khatamTracks.data.isNullOrEmpty()) {
            val size = state.value.khatamTracks.data?.size ?: 0
            for (i in 0..<size) {
                if (state.value.khatamTracks.data?.get(i)?.id == state.value.currentTrack.id && i < (size - 1)) {
                    playVideo(
                        state.value.khatamTracks.data?.get(i + 1) ?: TracksDtoItem(),
                        videoType = Enums.VideoType.VideoContent.name
                    )
                    return true
                }
            }
        }

        if (!state.value.downloadedTracks.data.isNullOrEmpty()) {
            val size = state.value.downloadedTracks.data?.size ?: 0
            for (i in 0..<size) {
                if (state.value.downloadedTracks.data?.get(i)?.id == state.value.currentTrack.id && i < (size - 1)) {
                    playVideo(
                        state.value.downloadedTracks.data?.get(i + 1) ?: TracksDtoItem(),
                        videoType = Enums.VideoType.VideoContent.name
                    )
                    return true
                }
            }
        }

        return false
    }

}