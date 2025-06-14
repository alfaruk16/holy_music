package com.holymusic.app.features.presentation.album_videos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
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
import com.holymusic.app.core.components.BottomPlayerView
import com.holymusic.app.core.components.Loader
import com.holymusic.app.core.components.video_player.VideoPlayerView
import com.holymusic.app.core.theme.BackGroundColor
import com.holymusic.app.core.util.Enums
import com.holymusic.app.features.data.remote.model.AlbumDtoItem
import com.holymusic.app.features.data.remote.model.AlbumTrackDtoItem
import com.holymusic.app.features.data.remote.model.CategoryDtoItem
import com.holymusic.app.features.presentation.album_audios.components.AlbumView
import com.holymusic.app.features.presentation.album_videos.components.AlbumVideos
import com.holymusic.app.features.presentation.video_album.components.VideoAlbum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumVideosScreen(
    modifier: Modifier = Modifier,
    viewModel: AlbumVideosViewModel = hiltViewModel(),
    category: CategoryDtoItem? = null,
    navToContent: (AlbumTrackDtoItem) -> Unit,
    navController: NavController,
    scrollState: LazyListState = rememberLazyListState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    navToChoosePlan: () -> Unit,
    navToLogin: () -> Unit
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
                    videoType = Enums.VideoType.Album.name
                )
        },
        scaffoldState = scaffoldState,
        sheetPeekHeight = if (!state.currentTrack.streamUrl.isNullOrEmpty()) 60.dp else 0.dp,
        sheetShape = RectangleShape
    ) { paddingValues ->

        if (state.isLoading) {
            Loader(paddingValues)
        }

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
        ) {

            LazyColumn(
                modifier = Modifier,
                state = scrollState
            ) {

                item {
                    Spacer(modifier = Modifier.height(15.dp))
                }
                item {
                    AlbumView(album = state.currentAlbum, videoAlbum = true)
                }
                item {
                    Spacer(modifier = Modifier.height(15.dp))
                }
                item {
                    AlbumVideos(
                        tracks = state.tracks,
                        playingId = state.playingId,
//                            play = viewModel::playAudio,
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
                        showCount = state.showCount,
                        showMore = viewModel::showMore
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
                item {
                    if (state.albumList.data != null)
                        LazyRow(contentPadding = PaddingValues(horizontal = 5.dp)) {
                            items(count = state.albumList.data?.size ?: 0) {
                                VideoAlbum(
                                    state.albumList.data?.get(it) ?: AlbumDtoItem(),
                                    navToContent = { album ->
                                            viewModel.albumSelected(album = album)
                                            scope.launch {
                                                viewModel.scrollToTop(0, scrollState)
                                            }
                                    }
                                )
                            }
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

    }


}
