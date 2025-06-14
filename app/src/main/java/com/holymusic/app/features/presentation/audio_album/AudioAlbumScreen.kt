package com.holymusic.app.features.presentation.audio_album

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.holymusic.app.R
import com.holymusic.app.core.components.AppBar
import com.holymusic.app.core.components.BottomPlayerView
import com.holymusic.app.core.components.GridView
import com.holymusic.app.core.components.Loader
import com.holymusic.app.core.components.PageIndicatorView
import com.holymusic.app.core.theme.Primary
import com.holymusic.app.features.data.remote.model.AlbumDtoItem
import com.holymusic.app.features.data.remote.model.CategoryDtoItem
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.data.remote.model.toTrackDtoItem
import com.holymusic.app.features.presentation.audio_album.components.AudioAlbum
import kotlinx.coroutines.delay

@Composable
fun AudioAlbumScreen(
        modifier: Modifier = Modifier,
        viewModel: AudioAlbumViewModel = hiltViewModel(),
        category: CategoryDtoItem? = null,
        navToContent: (AlbumDtoItem) -> Unit,
        navController: NavController,
        navToAudioPlayer: (CategoryDtoItem, TracksDtoItem) -> Unit,
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
        val width = LocalConfiguration.current.screenWidthDp

        val pagerState = com.google.accompanist.pager.rememberPagerState(initialPage = 0)

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

            LazyColumn(modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 5.dp)
            ) {

                item {
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
                                        .padding(horizontal = 5.dp, vertical = 10.dp)
                                        .width(width = width.dp)
                                        .height(width.dp)
                                        .clip(shape = RoundedCornerShape(15.dp))
                                        .clickable {
                                            println(item)
                                            navToAudioPlayer(
                                                category ?: CategoryDtoItem(
                                                    name = "Audio",
                                                    icon = R.drawable.audio
                                                ),
                                                item?.toTrackDtoItem() ?: TracksDtoItem()
                                            )
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
                    GridView(count = state.albums.data?.size ?: 0, grid = 3) {
                        AudioAlbum(state.albums.data?.get(it) ?: AlbumDtoItem(), navToContent)
                    }
                }

                item{
                    Spacer(modifier = Modifier.height(5.dp))
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
