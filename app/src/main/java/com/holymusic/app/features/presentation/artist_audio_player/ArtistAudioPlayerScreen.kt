package com.holymusic.app.features.presentation.artist_audio_player

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.holymusic.app.MainActivity
import com.holymusic.app.R
import com.holymusic.app.core.components.AppBar
import com.holymusic.app.core.components.BottomPlayerView
import com.holymusic.app.core.exoplayer.toSong
import com.holymusic.app.core.theme.BackGroundColor
import com.holymusic.app.core.theme.Gray
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.features.data.remote.model.CategoryDtoItem
import com.holymusic.app.features.presentation.artist_audio_player.components.AudioPlayerView
import com.holymusic.app.features.presentation.artist_audios.components.ArtistAudios
import com.holymusic.app.features.presentation.audio_artists.components.AudioArtists
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("ContextCastToActivity")
@Composable
fun ArtistAudioPlayerScreen(
    modifier: Modifier = Modifier,
    viewModel: ArtistAudioPlayerViewModel = hiltViewModel(),
    category: CategoryDtoItem? = null,
    navController: NavController,
    scope: CoroutineScope = rememberCoroutineScope(),
    scrollState: LazyListState = rememberLazyListState(),
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
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                state = scrollState,
                modifier = Modifier
                    .weight(1f)
                    .background(color = MaterialTheme.colorScheme.background)
            ) {


                item {
                    AudioPlayerView(
                        track = state.track,
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
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
                item {
                    ArtistAudios(
                        tracks = state.tracks,
                        play = viewModel::playAudio,
                        navToContent = {
                            scope.launch {
                                viewModel.scrollToTop(0, scrollState)
                            }
                        },
                        showCount = state.showCount,
                        showMore = viewModel::showMore,
                        select = viewModel::updateSelection,
                        audioExoPlayer = viewModel.audioExoPlayer,
                        navToChoosePlan = navToChoosePlan
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(15.dp))
                }

                if (!state.artistList.data.isNullOrEmpty())
                    item {
                        Text(
                            text = stringResource(id = R.string.popular_artist),
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .padding(start = 15.dp)
                                .fillMaxWidth(),
                            style = Typography.titleLarge
                        )
                    }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }

                item {
                    AudioArtists(
                        artistList = state.artistList,
                        navToContent = { artist ->
                            viewModel.artistSelected(artist)
                            scope.launch {
                                viewModel.scrollToTop(1, scrollState)
                            }
                        },
                        currentArtistId = state.currentArtistId
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(15.dp))
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
                hasFavorite = if (state.track.id == viewModel.audioExoPlayer.curPlayingSong.value?.toSong()?.mediaId) state.isFavorite else null
            )
        }
    }

}
