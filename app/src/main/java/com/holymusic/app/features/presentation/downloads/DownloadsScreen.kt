package com.holymusic.app.features.presentation.downloads

import android.annotation.SuppressLint
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.holymusic.app.core.components.AppBar
import com.holymusic.app.core.components.AudioVideoTab
import com.holymusic.app.core.components.BottomPlayerView
import com.holymusic.app.core.components.Loader
import com.holymusic.app.core.components.video_player.VideoPlayerView
import com.holymusic.app.core.theme.BackGroundColor
import com.holymusic.app.core.util.Enums
import com.holymusic.app.features.data.remote.model.CategoryDtoItem
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.presentation.downloads.components.Audios
import com.holymusic.app.features.presentation.downloads.components.DeleteConfirmation
import com.holymusic.app.features.presentation.downloads.components.Videos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DownloadsScreen(
    modifier: Modifier = Modifier,
    viewModel: DownloadsViewModel = hiltViewModel(),
    category: CategoryDtoItem? = null,
    navToContent: (TracksDtoItem) -> Unit,
    navToVideoPlayer: (TracksDtoItem) -> Unit,
    navController: NavController,
    scrollState: LazyListState = rememberLazyListState(),
    navToChoosePlan: () -> Unit,
    navToLogin: () -> Unit,
    scope: CoroutineScope = rememberCoroutineScope(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() }
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
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
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
                    videoType = Enums.VideoType.Download.name,
                    actions = false
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

        DisposableEffect(Unit) {
            onDispose {
                viewModel.pause()
            }
        }

        if (state.showSnackBar) {
            scope.launch {
                viewModel.closeSnackBar()
                snackBarHostState.showSnackbar(state.message)
            }
        }

        val isDelete = remember {
            mutableStateOf(false)
        }

        val deleteTrack = remember {
            mutableStateOf(TracksDtoItem())
        }

        if (isDelete.value)
            DeleteConfirmation(delete = {
                isDelete.value = false
                viewModel.delete(deleteTrack.value)
            }) {
                isDelete.value = false
            }

        Column(
            modifier = Modifier
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
                        0 -> Audios(
                            tracks = state.tracks,
                            play = viewModel::playAudio,
                            navToContent = navToContent,
                            showCount = state.showCount,
                            showMore = viewModel::showMore,
                            playingId = state.playingId,
                            delete = {
                                deleteTrack.value = it
                                isDelete.value = true
                            }
                        )

                        1 -> Videos(
                            tracks = state.videoTracks,
                            navToContent = { tracksDtoItem ->
                                scope.launch {
                                    viewModel.playVideo(tracksDtoItem.copy(contentBaseUrl = ""))
                                    delay(100)
                                    scaffoldState.bottomSheetState.expand()
                                }
                            },
                            showCount = state.showCountVideo,
                            showMore = viewModel::showMore,
                            playingId = -1,
                            delete = {
                                deleteTrack.value = it
                                isDelete.value = true
                            }
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
                navToLogin = navToLogin,
                updateDownload = {
                    viewModel.getTracks()
                }
            )
        }

    }

}
