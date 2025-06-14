package com.holymusic.app.features.presentation.my_favorites

import androidx.compose.foundation.background
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
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.holymusic.app.MainActivity
import com.holymusic.app.core.components.AppBar
import com.holymusic.app.core.components.AudioVideoTab
import com.holymusic.app.core.components.BottomPlayerView
import com.holymusic.app.core.components.Loader
import com.holymusic.app.core.components.video_player.VideoPlayerView
import com.holymusic.app.core.theme.BackGroundColor
import com.holymusic.app.core.util.Enums
import com.holymusic.app.features.data.remote.model.CategoryDtoItem
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.presentation.artist_audios.components.ArtistAudios
import com.holymusic.app.features.presentation.video_artists.components.ArtistVideos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyFavoritesScreen(
    modifier: Modifier = Modifier,
    viewModel: MyFavoritesViewModel = hiltViewModel(),
    category: CategoryDtoItem? = null,
    navToContent: (TracksDtoItem) -> Unit,
    navToVideoPlayer: (TracksDtoItem) -> Unit,
    navController: NavController,
    scrollState: LazyListState = rememberLazyListState(),
    navToChoosePlan: () -> Unit,
    navToLogin: () -> Unit,
    scope: CoroutineScope = rememberCoroutineScope()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()


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
                navController = navController,
                icon = category?.icon
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


        if (state.isLoading) {
            Loader(paddingValues)
        }

        LaunchedEffect(Unit) {
            viewModel.getTracks()
        }

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
        ) {

            LazyColumn(state = scrollState, modifier = Modifier.weight(1f)) {
                item { Spacer(modifier = Modifier.height(10.dp)) }
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
                                if(MainActivity.isPremium.value || (it.isPremium != true)) {
                                    scope.launch {
                                        viewModel.playVideo(it)
                                        delay(100)
                                        scaffoldState.bottomSheetState.expand()
                                    }
                                }else{
                                    navToChoosePlan()
                                }
                            },
                            showCount = state.showCountVideo,
                            showMore = viewModel::showMore,
                            playingId = ""
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }

            }

            BottomPlayerView(
                viewModel.audioExoPlayer,
                navToChoosePlan = navToChoosePlan,
                navToLogin = navToLogin
            )
        }

        LaunchedEffect(Unit) {
            viewModel.subscribeToObservers()
        }
    }

}
