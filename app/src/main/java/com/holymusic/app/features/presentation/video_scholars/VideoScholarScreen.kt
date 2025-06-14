package com.holymusic.app.features.presentation.video_scholars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.holymusic.app.MainActivity
import com.holymusic.app.R
import com.holymusic.app.core.components.AppBar
import com.holymusic.app.core.components.BottomPlayerView
import com.holymusic.app.core.components.Loader
import com.holymusic.app.core.components.video_player.VideoPlayerView
import com.holymusic.app.core.theme.BackGroundColor
import com.holymusic.app.core.theme.Gray
import com.holymusic.app.core.util.Enums
import com.holymusic.app.features.data.remote.model.ArtistDtoItem
import com.holymusic.app.features.data.remote.model.CategoryDtoItem
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.presentation.video_artists.components.ArtistVideos
import com.holymusic.app.features.presentation.video_scholars.components.ScholarArtist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoScholarScreen(
    modifier: Modifier = Modifier,
    viewModel: VideoScholarViewModel = hiltViewModel(),
    category: CategoryDtoItem? = null,
    navToContent: (ArtistDtoItem) -> Unit,
    navController: NavController,
    navToTrack: (TracksDtoItem) -> Unit,
    navToChoosePlan: () -> Unit,
    navToLogin: () -> Unit,
    scope: CoroutineScope = rememberCoroutineScope()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true
        )
    )

    BottomSheetScaffold(
        modifier = modifier.fillMaxSize(),
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
                    videoType = Enums.VideoType.Scholar.name
                )
        },
        scaffoldState = scaffoldState,
        sheetPeekHeight = if (!state.currentTrack.streamUrl.isNullOrEmpty()) 60.dp else 0.dp,
        sheetShape = RectangleShape
    ) { paddingValues ->

        if (state.isLoading) {
            Loader(paddingValues)
        }

        Column(modifier = Modifier.padding(paddingValues)) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(color = BackGroundColor),
            ) {

                LazyColumn {
                    item { Spacer(modifier = Modifier.height(10.dp)) }

                    item {
                        ArtistVideos(
                            tracks = state.tracks,
                            playingId = state.playingId,
//                            play = viewModel::playAudio,
                            navToContent = {
                                if(MainActivity.isPremium.value || (it.isPremium != true))
                                scope.launch {
                                    viewModel.playVideo(it)
                                    delay(100)
                                    scaffoldState.bottomSheetState.expand()
                                }
                            },
                            showCount = state.showCount,
                            showMore = viewModel::showMore,
                        )
                    }
                    item { Spacer(modifier = Modifier.height(10.dp)) }

                    if (!state.artist.data.isNullOrEmpty())
                        item {
                            Text(
                                text = stringResource(id = R.string.popular_scholar),
                                color = Gray,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.W600,
                                modifier = Modifier.padding(start = 15.dp)
                            )
                        }
                    item { Spacer(modifier = Modifier.height(10.dp)) }
                    item {
                        ScholarArtist(state.artist, navToContent)
                    }
                    item { Spacer(modifier = Modifier.height(10.dp)) }
                }
            }
            BottomPlayerView(
                viewModel.audioExoPlayer,
                navToChoosePlan = navToChoosePlan,
                navToLogin = navToLogin
            )
        }


    }
    LaunchedEffect(Unit) {
        viewModel.subscribeToObservers()
    }
}
