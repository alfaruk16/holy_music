package com.holymusic.app.features.presentation.album_audio_player

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.holymusic.app.MainActivity
import com.holymusic.app.core.components.AppBar
import com.holymusic.app.core.components.BottomPlayerView
import com.holymusic.app.core.exoplayer.toSong
import com.holymusic.app.core.theme.BackGroundColor
import com.holymusic.app.features.data.remote.model.AlbumDtoItem
import com.holymusic.app.features.data.remote.model.CategoryDtoItem
import com.holymusic.app.features.data.remote.model.toTrackDtoItem
import com.holymusic.app.features.presentation.album_audios.components.AlbumAudios
import com.holymusic.app.features.presentation.artist_audio_player.components.AudioPlayerView
import com.holymusic.app.features.presentation.audio_album.components.AudioAlbum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("ContextCastToActivity")
@Composable
fun AlbumAudioPlayerScreen(
    modifier: Modifier = Modifier,
    viewModel: AlbumAudioPlayerViewModel = hiltViewModel(),
    category: CategoryDtoItem? = null,
    navController: NavController,
    scope: CoroutineScope = rememberCoroutineScope(),
    scrollState: LazyGridState = rememberLazyGridState(),
    navToChoosePlan: () -> Unit,
    navToLogin: () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppBar(
                title = category?.name ?: "",
                icon = category?.icon,
                navController = navController
            )
        }
    ) { paddingValues ->

        val state by viewModel.state.collectAsStateWithLifecycle()
        val activity = LocalContext.current as Activity

        Column(
            modifier = Modifier
                .padding(paddingValues = paddingValues)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(color = MaterialTheme.colorScheme.background)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(horizontal = 5.dp),
                    state = scrollState
                ) {

                    item(span = { GridItemSpan(3) }) {
                        AudioPlayerView(
                            track = state.track.toTrackDtoItem(),
                            playAudio = viewModel::playAudio,
                            audioExoPlayer = viewModel.audioExoPlayer,
                            setFavorite = {
                                if (MainActivity.isLoggedIn) {
                                    viewModel.setFavorite(it)
                                } else {
                                    navToLogin()
                                }
                            },
                            isFavorite = state.isFavorite,
                            download = { file ->
                                if (MainActivity.isPremium.value) {
                                    viewModel.download(activity, file)
                                } else {
                                    navToChoosePlan()
                                }
                            },
                            downloadProgress = state.downloadProgress,
                            navToChoosePlan = navToChoosePlan
                        )
                    }
                    item(span = { GridItemSpan(3) }) {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    item(span = { GridItemSpan(3) }) {
                        AlbumAudios(
                            tracks = state.tracks,
                            play = viewModel::playAudio,
                            navToContent = {
                                scope.launch {
                                    viewModel.scrollToItem(
                                        0,
                                        scrollState
                                    )
                                }
                            },
                            showCount = state.showCount,
                            showMore = viewModel::showMore,
                            select = viewModel::updateSelection,
                            audioExoplayer = viewModel.audioExoPlayer,
                            navToChoosePlan = navToChoosePlan
                        )
                    }
                    item(span = { GridItemSpan(3) }) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (state.albumList.data != null)
                        item(span = { GridItemSpan(3) }) {
                            LazyRow {
                                items(count = state.albumList.data?.size ?: 0) {
                                    AudioAlbum(
                                        album = state.albumList.data?.get(it) ?: AlbumDtoItem(),
                                        navToContent = { album ->
                                                viewModel.albumSelected(album)
                                                scope.launch {
                                                    viewModel.scrollToItem(1, scrollState)
                                                }
                                        },
                                    )
                                }
                            }
                        }


                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            BottomPlayerView(
                viewModel.audioExoPlayer,
                navToChoosePlan = navToChoosePlan,
                navToLogin = navToLogin,
                updateFavorite = { isFavorite, trackId ->
                    viewModel.updateFavorite(isFavorite, trackId)
                    viewModel.checkIsDownloaded(trackId)
                },
                hasFavorite = if (state.track.trackId == viewModel.audioExoPlayer.curPlayingSong.value?.toSong()?.mediaId) state.isFavorite else null
            )
        }
    }

}
