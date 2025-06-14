package com.holymusic.app.features.presentation.home

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.activity.ComponentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.holymusic.app.MainActivity
import com.holymusic.app.core.exoplayer.viewmodels.AudioExoPlayer
import com.holymusic.app.core.theme.fourHundred
import com.holymusic.app.core.theme.hundredNinetyTwo
import com.holymusic.app.core.theme.replaceSize
import com.holymusic.app.core.util.AppConstants
import com.holymusic.app.core.util.Async
import com.holymusic.app.core.util.Enums
import com.holymusic.app.core.util.LocalConstant
import com.holymusic.app.features.data.remote.entity.SubStatus
import com.holymusic.app.features.data.remote.model.AlbumDto
import com.holymusic.app.features.data.remote.model.ArtistDto
import com.holymusic.app.features.data.remote.model.PromotionsDto
import com.holymusic.app.features.data.remote.model.TrackBillboardDto
import com.holymusic.app.features.data.remote.model.TrackBillboardDtoItem
import com.holymusic.app.features.data.remote.model.TracksDto
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.data.remote.model.toTrackDtoItem
import com.holymusic.app.features.domain.use_case.GetAlbumByTypeUseCase
import com.holymusic.app.features.domain.use_case.GetAlbumTrackUseCase
import com.holymusic.app.features.domain.use_case.GetArtistUseCase
import com.holymusic.app.features.domain.use_case.GetPromotionsUseCase
import com.holymusic.app.features.domain.use_case.GetScholarTrackByType
import com.holymusic.app.features.domain.use_case.GetSubscriptionsUseCase
import com.holymusic.app.features.domain.use_case.GetTrackBillboardUseCase
import com.holymusic.app.features.domain.use_case.GetTracksByTypeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    val audioExoPlayer: AudioExoPlayer, application: Application,
    private val getTrackBillboardUseCase: GetTrackBillboardUseCase,
    private val getArtistUseCase: GetArtistUseCase,
    private val getTracksUseCase: GetTracksByTypeUseCase,
    private val getAlbumByTypeUseCase: GetAlbumByTypeUseCase,
    private val getAlbumTrackUseCase: GetAlbumTrackUseCase,
    private val getScholarTrackByType: GetScholarTrackByType,
    private val getSubscriptionsUseCase: GetSubscriptionsUseCase,
    private val getPromotionsUseCase: GetPromotionsUseCase
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val preferences: SharedPreferences = application.getSharedPreferences(
        LocalConstant.sharedPreferences,
        Context.MODE_PRIVATE
    )

    private var reInit = false

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    fun init() {
        checkIsDark()
        checkSubscriptionStatus()
        getTrackBillboard()
        getArtist()
        getTrack()
        getVideoTrack()
        getAlbum()
        getVideoAlbum()
        getBabarTracksByAlbum(albumId = state.value.babarAlbum.id ?: "")
        getDeshTracksByAlbum(albumId = state.value.deshAlbum.id ?: "")
        getMaTracksByAlbum(albumId = state.value.maAlbum.id ?: "")
        getShishuTracksByAlbum(albumId = state.value.shishuAlbum.id ?: "")
        getJonoprioGajalByAlbum(albumId = state.value.jonoprioAlbum.id ?: "")
        getSelectedByAlbum(albumId = state.value.selectedAlbum.id ?: "")
        getZamanByAlbum(albumId = state.value.zamanAlbum.id ?: "")
        getSyedByAlbum(albumId = state.value.syedAlbum.id ?: "")
        getRayhanByAlbum(albumId = state.value.rayhanAlbum.id ?: "")
        getLatestReleaseByAlbum(albumId = state.value.latestReleaseAlbum.id ?: "")
        getPopularVideoByAlbum(albumId = state.value.popularVideoAlbum.id ?: "")
        getLatestReleaseVideoByAlbum(albumId = state.value.latestReleaseVideoAlbum.id ?: "")
        getScholarTrack()
    }

    private fun checkIsDark() {
        _state.value = state.value.copy(isDark = preferences.getInt(LocalConstant.isDark, 0))
    }

    fun isDark(isDark: Boolean) {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putInt(LocalConstant.isDark, if(isDark) 2 else 1)
        editor.apply()
        checkIsDark()
        MainActivity.isDark.value = isDark
    }

    private fun getPromotions() {
        getPromotionsUseCase().onEach { result ->
            when (result) {
                is Async.Success -> {
                    val data = result.data?.data?.filter { it.isActive }
                    _state.value = state.value.copy(
                        promotions = result.data?.copy(data = data) ?: PromotionsDto(),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun checkSubscriptionStatus() {

        if (MainActivity.isLoggedIn) {

            MainActivity.isPremium.value = preferences.getBoolean(LocalConstant.isPremium, false)

            getSubscriptionsUseCase.invoke(
                body = SubStatus(
                    MSISDN = (preferences.getString(
                        LocalConstant.mobile,
                        ""
                    ) ?: "")
                )
            ).onEach { result ->

                when (result) {
                    is Async.Success ->
                        if (!result.data.isNullOrEmpty()) {
                            var isPremium = false
                            for (item in result.data) {
                                if (item.regstatus == Enums.Subscriptions.Subscribed.name) {
                                    isPremium = true
                                    _state.value = state.value.copy(subStatusDtoItem = item)
                                }
                            }
                            MainActivity.isPremium.value = isPremium
                            val editor: SharedPreferences.Editor = preferences.edit()
                            editor.putBoolean(LocalConstant.isPremium, isPremium)
                            editor.apply()
                        }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun getPopularVideoByAlbum(albumId: String) {
        getAlbumTrackUseCase(
            id = albumId
        ).onEach { result ->

            val list = result.data?.data?.map {
                it.toTrackDtoItem()
                    .copy(imageUrl = replaceSize(it.imageUrl ?: "", fourHundred))
            }

            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(
                        popularVideoAlbumTrack = TracksDto(data = list, status = 200),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun getLatestReleaseVideoByAlbum(albumId: String) {
        getAlbumTrackUseCase(
            id = albumId
        ).onEach { result ->

            val list = result.data?.data?.map {
                it.toTrackDtoItem()
                    .copy(imageUrl = replaceSize(it.imageUrl ?: "", fourHundred))
            }

            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(
                        latestReleaseVideoTrack = TracksDto(data = list, status = 200),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun getRayhanByAlbum(albumId: String) {
        getAlbumTrackUseCase(
            id = albumId
        ).onEach { result ->

            val list = result.data?.data?.map {
                it.toTrackDtoItem()
                    .copy(imageUrl = replaceSize(it.imageUrl ?: "", hundredNinetyTwo))
            }

            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(
                        rayhanTracks = TracksDto(data = list, status = 200),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun getLatestReleaseByAlbum(albumId: String) {
        getAlbumTrackUseCase(
            id = albumId
        ).onEach { result ->

            val list = result.data?.data?.map {
                it.toTrackDtoItem()
                    .copy(imageUrl = replaceSize(it.imageUrl ?: "", hundredNinetyTwo))
            }

            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(
                        latestReleaseTrack = TracksDto(data = list, status = 200),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun getSyedByAlbum(albumId: String) {
        getAlbumTrackUseCase(
            id = albumId
        ).onEach { result ->

            val list = result.data?.data?.map {
                it.toTrackDtoItem()
                    .copy(imageUrl = replaceSize(it.imageUrl ?: "", hundredNinetyTwo))
            }

            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(
                        syedTracks = TracksDto(data = list, status = 200),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun getZamanByAlbum(albumId: String) {
        getAlbumTrackUseCase(
            id = albumId
        ).onEach { result ->

            val list = result.data?.data?.map {
                it.toTrackDtoItem()
                    .copy(imageUrl = replaceSize(it.imageUrl ?: "", hundredNinetyTwo))
            }

            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(
                        zamanTracks = TracksDto(data = list, status = 200),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun getSelectedByAlbum(albumId: String) {
        getAlbumTrackUseCase(
            id = albumId
        ).onEach { result ->

            val list = result.data?.data?.map {
                it.toTrackDtoItem()
                    .copy(imageUrl = replaceSize(it.imageUrl ?: "", hundredNinetyTwo))
            }

            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(
                        selectedTracks = TracksDto(data = list, status = 200),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun getMobile() {
        _state.value =
            state.value.copy(mobile = preferences.getString(LocalConstant.mobile, "") ?: "")
    }

    private fun getScholarTrack() {
        getScholarTrackByType(type = AppConstants.typeVideo).onEach { result ->
            when (result) {
                is Async.Success -> {
                    val data = result.data?.data?.map {
                        it.copy(
                            imageUrl = replaceSize(
                                it.imageUrl ?: "",
                                fourHundred
                            )
                        )
                    }
                    _state.value = state.value.copy(
                        scholarTracks = result.data?.copy(data = data) ?: TracksDto(),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun getJonoprioGajalByAlbum(albumId: String) {
        getAlbumTrackUseCase(
            id = albumId
        ).onEach { result ->

            val list = result.data?.data?.map {
                it.toTrackDtoItem()
                    .copy(imageUrl = replaceSize(it.imageUrl ?: "", hundredNinetyTwo))
            }

            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(
                        jonoprioTracks = TracksDto(data = list, status = 200),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun getShishuTracksByAlbum(albumId: String) {
        getAlbumTrackUseCase(
            id = albumId
        ).onEach { result ->

            val list = result.data?.data?.map {
                it.toTrackDtoItem()
                    .copy(imageUrl = replaceSize(it.imageUrl ?: "", hundredNinetyTwo))
            }

            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(
                        shishuTracks = TracksDto(data = list, status = 200),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun getMaTracksByAlbum(albumId: String) {
        getAlbumTrackUseCase(
            id = albumId
        ).onEach { result ->

            val list = result.data?.data?.map {
                it.toTrackDtoItem()
                    .copy(imageUrl = replaceSize(it.imageUrl ?: "", hundredNinetyTwo))
            }

            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(
                        maTracks = TracksDto(data = list, status = 200),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun getBabarTracksByAlbum(albumId: String) {
        getAlbumTrackUseCase(
            id = albumId
        ).onEach { result ->

            val list = result.data?.data?.map {
                it.toTrackDtoItem()
                    .copy(imageUrl = replaceSize(it.imageUrl ?: "", hundredNinetyTwo))
            }

            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(
                        babarTracks = TracksDto(data = list, status = 200),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun getDeshTracksByAlbum(albumId: String) {
        getAlbumTrackUseCase(
            id = albumId
        ).onEach { result ->

            val list = result.data?.data?.map {
                it.toTrackDtoItem()
                    .copy(imageUrl = replaceSize(it.imageUrl ?: "", hundredNinetyTwo))
            }

            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(
                        deshTracks = TracksDto(data = list, status = 200),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun getTrackBillboard() {

        getTrackBillboardUseCase().onEach { result ->

            when (result) {
                is Async.Success -> {

                    val data =
                        result.data?.data?.filter { it.contentType == AppConstants.trackType && it.contentCategory == AppConstants.typeAudio }
                    val shortPlay =
                        result.data?.data?.filter { it.contentType == AppConstants.shortPlay }
                    val trending =
                        result.data?.data?.filter { it.contentType == AppConstants.trending }

                    _state.value = state.value.copy(
                        trackBillboard = result.data?.copy(data = data) ?: TrackBillboardDto(),
                        isLoading = false,
                        trending = trending ?: emptyList()
                    )
                    if (shortPlay?.isNotEmpty() == true) {
                        _state.value =
                            state.value.copy(shortPlay = shortPlay[shortPlay.indices.random()])
                    }
                }

                is Async.Error -> {
                    if (result.message == "401" || result.message == "403") {
                        logOut()
                    }
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun logOut() {
        if (!reInit) {
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.remove(LocalConstant.token)
            editor.apply()
            MainActivity.isLoggedIn = false
            MainActivity.token = LocalConstant.freeToken
            MainActivity.isPremium.value = false
            init()
            reInit = true
        }
    }

    private fun getArtist() {
        getArtistUseCase().onEach { result ->
            when (result) {
                is Async.Success -> {
                    val data = result.data?.data?.map {
                        it.copy(
                            imageUrl = replaceSize(
                                it.imageUrl ?: "",
                                hundredNinetyTwo
                            )
                        )
                    }
                    _state.value = state.value.copy(
                        artist = result.data?.copy(data = data) ?: ArtistDto(),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun getTrack() {
        getTracksUseCase(
            type = AppConstants.typeAudio,
            take = 50,
            page = state.value.page
        ).onEach { result ->
            when (result) {
                is Async.Success -> {

                    val list = result.data?.data?.map {
                        it.copy(
                            imageUrl = replaceSize(
                                it.imageUrl ?: "",
                                hundredNinetyTwo
                            )
                        )
                    }

                    val popular = list?.sortedByDescending { it.playCount }
                    val favorite = list?.sortedByDescending { it.totalFav }?.filter {
                        (it.totalFav ?: 0) > 0
                    }

                    _state.value = state.value.copy(
                        tracks = result.data?.copy(data = list) ?: TracksDto(),
                        popularTracks = result.data?.copy(
                            data = popular?.subList(
                                0,
                                popular.size.coerceAtMost(10)
                            )
                        ) ?: TracksDto(),
                        favoriteTracks = result.data?.copy(
                            data = favorite?.subList(
                                0,
                                favorite.size.coerceAtMost(10)
                            )
                        ) ?: TracksDto(),
                        isLoading = false
                    )

                    for (i in 0..((result.data?.totalRecords ?: 0) / 100))
                        updateLocalAudios(i + 1)
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun updateLocalAudios(page: Int) {

        getTracksUseCase(
            type = AppConstants.typeAudio,
            take = 100,
            page = page
        ).onEach { result ->
            when (result) {
                is Async.Success -> {}

                else -> {}
            }
        }.launchIn(serviceScope)

    }

    private fun updateLocalVideos(page: Int) {

        getTracksUseCase(
            type = AppConstants.typeVideo,
            take = 100,
            page = page
        ).onEach { result ->
            when (result) {
                is Async.Success -> {}

                else -> {}
            }
        }.launchIn(serviceScope)

    }

    private fun getVideoTrack() {
        getTracksUseCase(type = AppConstants.typeVideo, take = 50).onEach { result ->
            when (result) {
                is Async.Success -> {

                    val list = result.data?.data
                        ?.map {
                            it.copy(imageUrl = replaceSize(it.imageUrl ?: "", fourHundred))
                        }

                    val popular = list?.sortedByDescending { it.playCount }
                    val favorite = list?.sortedByDescending { it.totalFav }?.filter {
                        (it.totalFav ?: 0) > 0
                    }

                    _state.value = state.value.copy(
                        videoTracks = result.data?.copy(data = list) ?: TracksDto(),
                        popularVideoTracks = result.data?.copy(
                            data = popular?.subList(
                                0,
                                popular.size.coerceAtMost(10)
                            )
                        ) ?: TracksDto(),
                        favoriteVideoTracks = result.data?.copy(
                            data = favorite?.subList(
                                0,
                                favorite.size.coerceAtMost(10)
                            )
                        ) ?: TracksDto(),
                        isLoading = false
                    )
                    for (i in 0..(result.data?.totalRecords ?: 0) / 100)
                        updateLocalVideos(i + 1)
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun getAlbum() {
        getAlbumByTypeUseCase(AppConstants.typeAudio).onEach { result ->
            when (result) {
                is Async.Success -> {

                    val list = result.data?.data?.map {
                        it.copy(
                            imageUrl = replaceSize(
                                it.imageUrl ?: "",
                                hundredNinetyTwo
                            )
                        )
                    }
                    _state.value = state.value.copy(
                        albums = result.data?.copy(data = list) ?: AlbumDto(),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun getVideoAlbum() {
        getAlbumByTypeUseCase(AppConstants.typeVideo).onEach { result ->
            when (result) {
                is Async.Success -> {

                    val list = result.data?.data?.map {
                        it.copy(
                            imageUrl = replaceSize(
                                it.imageUrl ?: "",
                                fourHundred
                            )
                        )
                    }

                    _state.value = state.value.copy(
                        videoAlbums = result.data?.copy(data = list) ?: AlbumDto(),
                        isLoading = false
                    )
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun loaderClosed() {
        _state.value = state.value.copy(isLoaderClosed = true)
    }

    fun closeShortPlay() {
        _state.value = state.value.copy(shortPlay = TrackBillboardDtoItem())
    }

    fun dismissPromotions(id: Int) {
        val list = state.value.promotions.data?.filter { it.id != id }
        _state.value = state.value.copy(promotions = state.value.promotions.copy(data = list))
    }

    fun closePromotions() {
        _state.value = state.value.copy(promotions = PromotionsDto())
    }

    fun closeMiniPlayer() {
        _state.value = state.value.copy(currentTrack = TracksDtoItem())
    }

    fun playVideo(track: TracksDtoItem, videoType: String) {
        viewModelScope.launch {
            if (!state.value.currentTrack.id.isNullOrEmpty()) {
                _state.value = state.value.copy(currentTrack = TracksDtoItem())
                delay(100)
            }
            _state.value = state.value.copy(currentTrack = track, videoType = videoType)
        }
    }

    fun closeService() {
        serviceJob.cancel()
    }

}