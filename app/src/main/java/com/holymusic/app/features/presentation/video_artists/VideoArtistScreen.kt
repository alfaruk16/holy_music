package com.holymusic.app.features.presentation.video_artists

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.holymusic.app.MainActivity
import com.holymusic.app.R
import com.holymusic.app.core.components.ActionItem
import com.holymusic.app.core.components.AppBar
import com.holymusic.app.core.components.BottomPlayerView
import com.holymusic.app.core.components.Loader
import com.holymusic.app.core.components.NoInternet
import com.holymusic.app.core.components.PageIndicatorView
import com.holymusic.app.core.components.video_player.VideoPlayerView
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.core.util.Enums
import com.holymusic.app.core.util.isOnline
import com.holymusic.app.features.data.remote.model.ArtistDtoItem
import com.holymusic.app.features.data.remote.model.CategoryDtoItem
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.data.remote.model.toTrackDtoItem
import com.holymusic.app.features.presentation.ScreenRoute
import com.holymusic.app.features.presentation.Screens
import com.holymusic.app.features.presentation.video_artists.components.ArtistVideos
import com.holymusic.app.features.presentation.video_artists.components.VideoArtists
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalPagerApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun VideoArtistScreen(
    modifier: Modifier = Modifier,
    viewModel: VideoArtistViewModel = hiltViewModel(),
    category: CategoryDtoItem? = null,
    navToContent: (ArtistDtoItem) -> Unit,
    navController: NavController,
    navToTrack: (TracksDtoItem) -> Unit,
    navToChoosePlan: () -> Unit,
    navToLogin: () -> Unit,
    navToSearch: () -> Unit,
    scope: CoroutineScope = rememberCoroutineScope()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val width = LocalConfiguration.current.screenWidthDp
    val context = LocalContext.current

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
                navController = navController,
                actions = listOf(ActionItem(icon = Icons.Filled.Search, action = {
                    navToSearch()
                }))
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
            if (state.isLoading) {
                viewModel.init()
            }
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

        if (!isOnline(context))
            Box(modifier = Modifier.padding(paddingValues)) {
                NoInternet {
                    if (isOnline(context)) {
                        navController.navigate(ScreenRoute.VideoArtist.route) {
                            popUpTo(Screens.HOME_SCREEN)
                        }
                    }
                }
            }
        else if (state.isLoading) {
            Loader(paddingValues)
        } else
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background)
            ) {

                LazyColumn(modifier = Modifier.weight(1f)) {
                    item {
                        if (!state.trackBillboard.data.isNullOrEmpty())
                            HorizontalPager(count = remember {
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
                        ArtistVideos(
                            tracks = state.tracks,
                            playingId = state.playingId,
//                            play = viewModel::playAudio,
                            navToContent = {
                                if (MainActivity.isPremium.value || (it.isPremium != true)) {
                                    scope.launch {
                                        viewModel.playVideo(it)
                                        delay(100)
                                        scaffoldState.bottomSheetState.expand()
                                    }
                                } else {
                                    navToChoosePlan()
                                }
                            },
                            showCount = state.showCount,
                            showMore = viewModel::showMore,
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    item {
                        if (!state.artist.data.isNullOrEmpty())
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
                        VideoArtists(state.artist, navToContent)
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
