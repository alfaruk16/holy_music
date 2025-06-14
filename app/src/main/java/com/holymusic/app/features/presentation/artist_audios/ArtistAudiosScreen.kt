package com.holymusic.app.features.presentation.artist_audios

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.holymusic.app.MainActivity
import com.holymusic.app.R
import com.holymusic.app.core.components.AppBar
import com.holymusic.app.core.components.AudioVideoTab
import com.holymusic.app.core.components.BottomPlayerView
import com.holymusic.app.core.components.video_player.VideoPlayerView
import com.holymusic.app.core.theme.BackGroundColor
import com.holymusic.app.core.theme.Gray
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.core.util.AppConstants
import com.holymusic.app.core.util.Enums
import com.holymusic.app.features.data.remote.model.CategoryDtoItem
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.presentation.artist_audios.components.ArtistAudios
import com.holymusic.app.features.presentation.artist_audios.components.ArtistView
import com.holymusic.app.features.presentation.audio_artists.components.AudioArtists
import com.holymusic.app.features.presentation.video_artists.components.ArtistVideos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistAudiosScreen(
    modifier: Modifier = Modifier,
    viewModel: ArtistAudiosViewModel = hiltViewModel(),
    category: CategoryDtoItem? = null,
    navToContent: (TracksDtoItem) -> Unit,
    navToVideoPlayer: (TracksDtoItem) -> Unit,
    navController: NavController,
    scope: CoroutineScope = rememberCoroutineScope(),
    scrollState: LazyListState = rememberLazyListState(),
    navToChoosePlan: () -> Unit,
    navToLogin: () -> Unit
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    val list = remember {
        listOf(AppConstants.audio, AppConstants.video)
    }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true
        )
    )

    BottomSheetScaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppBar(
                title = category?.name ?: "",
                icon = category?.icon,
                navController = navController
            )
        },
        sheetDragHandle = {},
        sheetMaxWidth = Dp.Infinity,
        sheetContent = {
            if (!state.currentTrack.streamUrl.isNullOrEmpty())
                VideoPlayerView(
                    track = state.currentTrack,
                    navController = navController,
                    sheetState = scaffoldState.bottomSheetState,
                    close = { viewModel.closeMiniPlayer() },
                    navToLogin = navToLogin,
                    navToChoosePlan = navToChoosePlan,
                    audioPlayer = viewModel.audioExoPlayer,
                    videoType = Enums.VideoType.Artist.name
                )
        },
        scaffoldState = scaffoldState,
        sheetPeekHeight = if (!state.currentTrack.streamUrl.isNullOrEmpty()) 60.dp else 0.dp,
        sheetShape = RectangleShape

    ) { paddingValues ->

        LaunchedEffect(Unit) {
            viewModel.subscribeToObservers()
        }

        Column(modifier = Modifier.padding(paddingValues)) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(color = MaterialTheme.colorScheme.background),
            ) {

                LazyColumn(state = scrollState) {

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    item {
                        ArtistView(state.currentArtist)
                    }
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    item {
                        AudioVideoTab(selectedTab = state.selectedTab) {
                            viewModel.tabChanged(it)
                        }
                    }
                    item {
                        when (state.selectedTab) {
                            0 -> ArtistAudios(
                                tracks = state.tracks,
                                play = viewModel::playAudio,
                                navToContent = navToContent,
                                showCount = state.showCount,
                                showMore = viewModel::showMore,
                                audioExoPlayer = viewModel.audioExoPlayer,
                                navToChoosePlan = navToChoosePlan
                            )

                            1 -> ArtistVideos(
                                tracks = state.videoTracks,
                                navToContent = {
                                    scope.launch {
                                        if(MainActivity.isPremium.value || (it.isPremium != true)) {
                                            viewModel.playVideo(it)
                                            delay(100)
                                            scaffoldState.bottomSheetState.expand()
                                        }else{
                                            navToChoosePlan()
                                        }
                                    }
                                },
                                showCount = state.showCountVideo,
                                showMore = viewModel::showMore,
                                playingId = "",
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(15.dp))
                    }

                    if (!state.artistList.data.isNullOrEmpty())
                        item {
                            Text(
                                text = stringResource(id = R.string.popular_artist),
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(start = 15.dp),
                                style = Typography.titleLarge
                            )
                        }
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    item {
                        AudioArtists(
                            state.artistList,
                            navToContent = { artist ->
                                    viewModel.artistSelected(artist)
                                    scope.launch {
                                        viewModel.scrollToItem(0, scrollState)
                                    }
                            },
                            currentArtistId = state.currentArtistId
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            BottomPlayerView(
                viewModel.audioExoPlayer,
                navToChoosePlan = navToChoosePlan,
                navToLogin = navToLogin
            )
        }

    }

}
