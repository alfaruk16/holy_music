package com.holymusic.app.features.presentation.audio_artists

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.holymusic.app.R
import com.holymusic.app.core.components.ActionItem
import com.holymusic.app.core.components.AppBar
import com.holymusic.app.core.components.BottomPlayerView
import com.holymusic.app.core.components.Loader
import com.holymusic.app.core.components.NoInternet
import com.holymusic.app.core.components.PageIndicatorView
import com.holymusic.app.core.theme.BackGroundColor
import com.holymusic.app.core.theme.Gray
import com.holymusic.app.core.theme.GrayExtraLight
import com.holymusic.app.core.theme.Primary
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.core.util.isOnline
import com.holymusic.app.features.data.remote.model.ArtistDtoItem
import com.holymusic.app.features.data.remote.model.CategoryDtoItem
import com.holymusic.app.features.data.remote.model.TrackBillboardDtoItem
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.data.remote.model.toTrackDtoItem
import com.holymusic.app.features.presentation.ScreenRoute
import com.holymusic.app.features.presentation.Screens
import com.holymusic.app.features.presentation.artist_audios.components.ArtistAudios
import com.holymusic.app.features.presentation.audio_artists.components.AudioArtists
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.delay

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AudioArtistScreen(
    modifier: Modifier = Modifier,
    viewModel: AudioArtistViewModel = hiltViewModel(),
    category: CategoryDtoItem? = null,
    navToContent: (ArtistDtoItem) -> Unit,
    navController: NavController,
    navToTrack: (TracksDtoItem) -> Unit,
    navToChoosePlan: () -> Unit,
    navToLogin: () -> Unit,
    navToSearch: () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppBar(
                title = category?.name ?: "",
                icon = category?.icon,
                navController = navController,
                actions = listOf(ActionItem(icon = Icons.Filled.Search, action = {
                    navToSearch()
                }))
            )
        }
    ) { paddingValues ->

        val state by viewModel.state.collectAsStateWithLifecycle()
        val width = LocalConfiguration.current.screenWidthDp
        val context = LocalContext.current

        val pagerState = com.google.accompanist.pager.rememberPagerState(initialPage = 0)


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
                        navController.navigate(ScreenRoute.AudioArtist.route) {
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
                    .background(color = MaterialTheme.colorScheme.background).padding(paddingValues)
            ) {

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {

                    item {
                        if (!state.trackBillboard.data.isNullOrEmpty())
                            com.google.accompanist.pager.HorizontalPager(count = remember {
                                if (state.trackBillboard.data != null) state.trackBillboard.data?.size
                                    ?: 0 else 0
                            }, state = pagerState) {
                                val item =
                                    state.trackBillboard.data?.get(it)
                                        ?: TrackBillboardDtoItem()
                                AsyncImage(
                                    model = item.contentBaseUrl + item.imageUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .width(width = width.dp)
                                        .height(width.dp)
                                        .clip(shape = RoundedCornerShape(15.dp))
                                        .clickable { navToTrack(item.toTrackDtoItem()) }
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
                                            selectedColor = Primary,
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
                        ArtistAudios(
                            tracks = state.tracks,
                            play = viewModel::playAudio,
                            navToContent = navToTrack,
                            showCount = state.showCount,
                            showMore = viewModel::showMore,
                            audioExoPlayer = viewModel.audioExoPlayer,
                            navToChoosePlan = navToChoosePlan
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
                        AudioArtists(state.artist, navToContent)
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
