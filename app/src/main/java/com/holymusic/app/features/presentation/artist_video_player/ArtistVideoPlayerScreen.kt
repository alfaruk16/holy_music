package com.holymusic.app.features.presentation.artist_video_player

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.holymusic.app.MainActivity
import com.holymusic.app.R
import com.holymusic.app.core.components.Loader
import com.holymusic.app.core.components.VideoPlayer
import com.holymusic.app.core.theme.BackGroundColor
import com.holymusic.app.core.theme.GradientColor1
import com.holymusic.app.core.theme.GradientColor2
import com.holymusic.app.core.theme.Gray
import com.holymusic.app.core.theme.GrayLight
import com.holymusic.app.core.theme.Primary
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.core.util.AppConstants
import com.holymusic.app.features.data.local.model.FileItem
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.domain.services.Share
import com.holymusic.app.features.presentation.Screens
import com.holymusic.app.features.presentation.audio_artists.components.AudioArtists
import com.holymusic.app.features.presentation.video_artists.components.ArtistVideos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("ContextCastToActivity")
@Composable
fun ArtistVideoPlayerScreen(
    modifier: Modifier = Modifier,
    viewModel: ArtistVideoPlayerViewModel = hiltViewModel(),
    navToContent: (TracksDtoItem) -> Unit,
    navController: NavController,
    scope: CoroutineScope = rememberCoroutineScope(),
    scrollState: LazyListState = rememberLazyListState(),
    navToChoosePlan: () -> Unit,
    navToLogin: () -> Unit
) {

    Scaffold(
        modifier = modifier.fillMaxSize(),
//        topBar = {
//            AppBar(
//                title = category?.name,
//                image = category.contentBaseUrl + category.imageUrl,
//                navController = navController
//            )
//        }
    ) { paddingValues ->

        val state by viewModel.state.collectAsStateWithLifecycle()
        val width = LocalConfiguration.current.screenWidthDp / 16 * 9
        val context = LocalContext.current
        val activity = LocalContext.current as Activity

        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {

            if (state.track.streamUrl != null)

                Box(
                    modifier = Modifier
                        .height(width.dp)
                        .background(color = Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    VideoPlayer(
                        state.track.contentBaseUrl + state.track.streamUrl,
                        viewModel::addPlayCount,
                        navController
                    )
                }
            if (!state.track.title.isNullOrEmpty())
                Card {
                    Column(
                        modifier = Modifier
                            .background(color = MaterialTheme.colorScheme.primaryContainer)
                            .fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = state.track.title ?: "",
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier
                                    .padding(top = 10.dp, start = 15.dp)
                                    .weight(1f),
                                maxLines = 1,
                                style = Typography.titleMedium
                            )
                            if (!state.track.contentBaseUrl.isNullOrEmpty())
                                Text(
                                    text = (if (state.track.playCount != null) state.track.playCount.toString() else "1") + " " + stringResource(
                                        id = R.string.views
                                    ),
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    modifier = Modifier.padding(end = 15.dp),
                                    style = Typography.displaySmall
                                )
                        }

                        Row(
                            modifier = Modifier.padding(horizontal = 15.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = state.track.artistName ?: "",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier
                                    .padding(top = 5.dp, bottom = 10.dp)
                                    .weight(1f),
                                maxLines = 1,
                                style = Typography.displaySmall
                            )
                            if (!state.track.contentBaseUrl.isNullOrEmpty())
                                Icon(if (state.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = null,
                                    tint = if (state.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier
                                        .clip(shape = CircleShape)
                                        .clickable {
                                            if (MainActivity.isLoggedIn) {
                                                viewModel.setFavorite(state.track)
                                            } else {
                                                navToLogin()
                                            }
                                        }

                                )
                            Spacer(modifier = Modifier.width(10.dp))
                            if (!state.track.contentBaseUrl.isNullOrEmpty())
                                Box(
                                    modifier = Modifier
                                        .clip(shape = CircleShape)
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.onTertiary
                                                )
                                            )
                                        )
                                        .clip(shape = CircleShape)
                                        .clickable {
                                            if (MainActivity.isPremium.value) {
                                                val track = state.track
                                                viewModel.download(
                                                    activity,
                                                    FileItem(
                                                        id = track.id ?: "",
                                                        url = track.contentBaseUrl + track.streamUrl,
                                                        name = track.title ?: "",
                                                        mimeType = AppConstants.typeVideo,
                                                        contentBaseUrl = track.contentBaseUrl
                                                            ?: "",
                                                        imageUrl = track.imageUrl ?: "",
                                                        artistName = track.artistName ?: "",
                                                        description = track.about ?: "",
                                                        duration = track.duration ?: ""
                                                    )
                                                )
                                            } else {
                                                navToChoosePlan()
                                            }
                                        }
                                        .size(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {

                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawArc(
                                            color = Primary,
                                            -90f,
                                            state.downloadProgress.toFloat() / 100 * 360f,
                                            useCenter = false,
                                            style = Stroke(15f, cap = StrokeCap.Round)
                                        )
                                    }

                                    Icon(
                                        Icons.Filled.Download,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            Spacer(modifier = Modifier.width(10.dp))
                            if (!state.track.contentBaseUrl.isNullOrEmpty())
                                Icon(Icons.Filled.Share, contentDescription = null,
                                    modifier = Modifier
                                        .clickable {
                                            Share.shareData(
                                                context = context,
                                                title = state.track.title ?: "",
                                                screen = Screens.ARTIST_VIDEO_PLAYER_SCREEN,
                                                id = state.track.id ?: ""
                                            )
                                        }
                                        .size(20.dp))
                        }
                    }
                }

            LazyColumn(
                modifier = Modifier.weight(1f),
                state = scrollState
            ) {

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
                item {
                    ArtistVideos(
                        tracks = state.tracks,
                        playingId = state.track.id ?: "",
                        navToContent = {
                            if (it.id != state.track.id) {
                                navToContent(it)
                            }
                        },
                        showCount = state.showCount,
                        showMore = viewModel::showMore,
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
                                    viewModel.scrollToTop(0, scrollState)
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
        if (state.isLoading) Loader(paddingValues = paddingValues)
    }
}

