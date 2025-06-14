package com.holymusic.app.features.presentation.video_album

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.holymusic.app.core.components.AppBar
import com.holymusic.app.core.components.BottomPlayerView
import com.holymusic.app.core.components.GridView
import com.holymusic.app.core.components.Loader
import com.holymusic.app.core.components.PageIndicatorView
import com.holymusic.app.core.components.video_player.VideoPlayerView
import com.holymusic.app.core.theme.BackGroundColor
import com.holymusic.app.core.theme.GrayExtraLight
import com.holymusic.app.core.theme.Primary
import com.holymusic.app.core.util.Enums
import com.holymusic.app.features.data.remote.model.AlbumDtoItem
import com.holymusic.app.features.data.remote.model.CategoryDtoItem
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.data.remote.model.toTrackDtoItem
import com.holymusic.app.features.presentation.video_album.components.VideoAlbum
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VideoAlbumScreen(
    modifier: Modifier = Modifier,
    viewModel: VideoAlbumViewModel = hiltViewModel(),
    category: CategoryDtoItem? = null,
    navToContent: (AlbumDtoItem) -> Unit,
    navController: NavController,
    navToVideoPlayer: (CategoryDtoItem, TracksDtoItem) -> Unit,
    navToChoosePlan: () -> Unit,
    navToLogin: () -> Unit,
    scope: CoroutineScope = rememberCoroutineScope()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val width = LocalConfiguration.current.screenWidthDp

    val pagerState = com.google.accompanist.pager.rememberPagerState(initialPage = 0)

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
        sheetShape = RectangleShape,
    ) { paddingValues ->

        LaunchedEffect(Unit) {
            while (true) {
                delay(5000)
                with(pagerState) {
                    val target = if (currentPage < pageCount - 1) currentPage + 1 else 0
                    tween<Float>(
                        durationMillis = 500,
                        easing = FastOutSlowInEasing
                    )
                    animateScrollToPage(page = target)
                }
            }
        }

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
                modifier = Modifier.weight(1f)
            ) {
                item {
                    if (state.albums.data != null)
                        if (!state.trackBillboard.data.isNullOrEmpty())
                            com.google.accompanist.pager.HorizontalPager(count = remember {
                                if (state.trackBillboard.data != null) state.trackBillboard.data?.size
                                    ?: 0 else 0
                            }, state = pagerState) {
                                val item = state.trackBillboard.data?.get(it)
                                AsyncImage(
                                    model = item?.contentBaseUrl + item?.imageUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .width(width = width.dp)
                                        .height(width.dp)
                                        .clip(shape = RoundedCornerShape(15.dp))
                                        .clickable {
                                            scope.launch {
                                                viewModel.playVideo(
                                                    item?.toTrackDtoItem() ?: TracksDtoItem()
                                                )
                                                delay(100)
                                                scaffoldState.bottomSheetState.expand()
                                            }
                                        }
                                )
                            }
                }
                item {
                    if (!state.trackBillboard.data.isNullOrEmpty())
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .padding(top = 8.dp, bottom = 24.dp)
                                .fillMaxWidth(),
                        ) {
                            for (i in 0 until pagerState.pageCount) {
                                val isSelected = i == pagerState.currentPage
                                Box(modifier = Modifier.padding(horizontal = 5.dp)) {
                                    PageIndicatorView(
                                        isSelected = isSelected,
                                        selectedColor = MaterialTheme.colorScheme.primary,
                                        defaultColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        defaultRadius = 8.dp,
                                        selectedLength = 8.dp,
                                        animationDurationInMillis = 300,
                                    )
                                }
                            }
                        }
                }
                item {
                    GridView(count = state.albums.data?.size ?: 0, grid = 1) {
                        VideoAlbum(state.albums.data?.get(it) ?: AlbumDtoItem(), navToContent)
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
    LaunchedEffect(Unit) {
        viewModel.subscribeToObservers()
    }
}