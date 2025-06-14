package com.holymusic.app.core.components.video_player

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.holymusic.app.MainActivity
import com.holymusic.app.R
import com.holymusic.app.core.exoplayer.isPlaying
import com.holymusic.app.core.exoplayer.viewmodels.AudioExoPlayer
import com.holymusic.app.core.theme.GradientColor1
import com.holymusic.app.core.theme.GradientColor2
import com.holymusic.app.core.theme.Gray
import com.holymusic.app.core.theme.GrayLight
import com.holymusic.app.core.theme.Primary
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.core.util.AppConstants
import com.holymusic.app.core.util.Enums
import com.holymusic.app.features.data.local.model.FileItem
import com.holymusic.app.features.data.remote.model.AlbumDtoItem
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.data.remote.model.toTrackDtoItem
import com.holymusic.app.features.domain.services.Share
import com.holymusic.app.features.presentation.Screens
import com.holymusic.app.features.presentation.album_videos.components.AlbumVideos
import com.holymusic.app.features.presentation.downloads.components.DeleteConfirmation
import com.holymusic.app.features.presentation.downloads.components.Videos
import com.holymusic.app.features.presentation.video_album.components.VideoAlbum
import com.holymusic.app.features.presentation.video_artists.components.ArtistVideos
import com.holymusic.app.features.presentation.video_artists.components.VideoArtists
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerView(
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: VideoPlayerViewModel = hiltViewModel(),
    scrollState: LazyListState = rememberLazyListState(),
    track: TracksDtoItem,
    navController: NavController,
    sheetState: SheetState,
    close: () -> Unit,
    navToLogin: () -> Unit,
    navToChoosePlan: () -> Unit,
    audioPlayer: AudioExoPlayer,
    videoType: String,
    actions: Boolean = true,
    backHandler: Boolean = true
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val width = LocalConfiguration.current.screenWidthDp
    val context = LocalContext.current
    val activity = LocalContext.current as Activity

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

    LaunchedEffect(Unit) {
        viewModel.init(track, videoType)
        viewModel.getArtist()
        if (audioPlayer.playbackState.value?.isPlaying == true) {
            audioPlayer.pause()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {

        if (!state.currentTrack.streamUrl.isNullOrEmpty())
            Player(
                state.currentTrack,
                viewModel::addPlayCount,
                navController,
                minimizable = {
                    scope.launch {
                        if (it) {
                            sheetState.partialExpand()
                        } else {
                            if (audioPlayer.playbackState.value?.isPlaying == true) {
                                audioPlayer.pause()
                            }
                            sheetState.expand()
                        }
                    }
                },
                sheetState = sheetState,
                close = {
                    viewModel.closePlayer()
                    close()
                },
                audioPlayer = audioPlayer,
                backHandler = backHandler,
                playOnFinished = viewModel::playOnFinished
            )
        else
            Box(
                modifier = Modifier
                    .height(if (sheetState.targetValue == SheetValue.Expanded) (width * 9 / 16).dp else 60.dp)
                    .width(if (sheetState.targetValue == SheetValue.Expanded) Dp.Infinity else 107.dp)
                    .background(Color.Black)
                    .fillMaxWidth()
            )

        LazyColumn(
            modifier = Modifier.weight(1f),
            state = scrollState
        ) {

            if (sheetState.targetValue == SheetValue.Expanded)
                item {
                    Box(
                        modifier = Modifier.background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = state.currentTrack.title ?: "",
                                    fontWeight = FontWeight.W600,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier
                                        .padding(top = 10.dp),
                                    maxLines = 1,
                                    style = Typography.displayMedium
                                )
                                Text(
                                    text = state.currentTrack.artistName ?: "",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    modifier = Modifier
                                        .padding(top = 5.dp, bottom = 10.dp),
                                    maxLines = 1,
                                    style = Typography.displaySmall
                                )

                            }

                            if (actions)
                                Column(
                                    modifier = Modifier.padding(start = 10.dp),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    if (!state.currentTrack.contentBaseUrl.isNullOrEmpty())
                                        Text(
                                            text = (if (state.currentTrack.playCount != null) state.currentTrack.playCount.toString() else "1") + " " + stringResource(
                                                id = R.string.views
                                            ),
                                            color = MaterialTheme.colorScheme.onSecondary,
                                            fontSize = 12.sp,
                                            maxLines = 1,
                                            style = Typography.displaySmall
                                        )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Row {
                                        if (!state.currentTrack.contentBaseUrl.isNullOrEmpty())
                                            Icon(if (state.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                                contentDescription = null,
                                                tint = if (state.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier
                                                    .clickable {
                                                        if (MainActivity.isLoggedIn) {
                                                            viewModel.setFavorite(state.currentTrack)
                                                        } else {
                                                            navToLogin()
                                                        }
                                                    }

                                            )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        if (!state.currentTrack.contentBaseUrl.isNullOrEmpty())
                                            Box(
                                                modifier = Modifier
                                                    .clip(shape = CircleShape)
                                                    .background(
                                                        brush = Brush.linearGradient(
                                                            colors = listOf(
                                                                MaterialTheme.colorScheme.tertiary, GradientColor2
                                                            )
                                                        )
                                                    )
                                                    .clickable {
                                                        if (MainActivity.isPremium.value == (state.currentTrack.isPremium == true)) {
                                                            viewModel.download(
                                                                activity, FileItem(
                                                                    id = state.currentTrack.id
                                                                        ?: "",
                                                                    url = state.currentTrack.contentBaseUrl + state.currentTrack.streamUrl,
                                                                    name = state.currentTrack.title
                                                                        ?: "",
                                                                    mimeType = AppConstants.typeVideo,
                                                                    contentBaseUrl = state.currentTrack.contentBaseUrl
                                                                        ?: "",
                                                                    imageUrl = state.currentTrack.imageUrl
                                                                        ?: "",
                                                                    artistName = state.currentTrack.artistName
                                                                        ?: "",
                                                                    description = state.currentTrack.about
                                                                        ?: "",
                                                                    duration = state.currentTrack.duration
                                                                        ?: ""
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
                                                if (!state.currentTrack.contentBaseUrl.isNullOrEmpty())
                                                    Icon(
                                                        Icons.Filled.Download,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.secondary,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                            }

                                        Spacer(modifier = Modifier.width(10.dp))
                                        if (!state.currentTrack.contentBaseUrl.isNullOrEmpty())
                                            Icon(
                                                Icons.Filled.Share, contentDescription = null,
                                                tint = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier
                                                    .clickable {
                                                        Share.shareData(
                                                            context = context,
                                                            title = state.currentTrack.title ?: "",
                                                            screen = Screens.ARTIST_VIDEO_PLAYER_SCREEN,
                                                            id = state.currentTrack.id ?: ""
                                                        )
                                                    }
                                                    .size(20.dp))
                                    }
                                }

                        }
                    }

                }
            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (videoType == Enums.VideoType.Artist.name && !state.artistTracks.data.isNullOrEmpty())
                item {
                    ArtistVideos(
                        tracks = state.artistTracks,
                        playingId = state.playingId,
                        navToContent = {
                            scope.launch {
                                viewModel.playVideo(it, Enums.VideoType.Artist.name)
                            }
                        },
                        showCount = state.showCountArtist,
                        showMore = viewModel::showMoreArtist,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

            if (videoType == Enums.VideoType.Artist.name && !state.artist.data.isNullOrEmpty())
                item {
                    Text(
                        text = stringResource(id = R.string.popular_artist),
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.W600,
                        modifier = Modifier.padding(start = 15.dp),
                        style = Typography.displayLarge
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    VideoArtists(state.artist, {
                        scope.launch {
                            viewModel.getArtistTracks(it.id ?: "")
                            viewModel.scrollToTop(scrollState)
                        }
                    })
                }

            if (!state.albumTracks.data.isNullOrEmpty())
                item {
                    AlbumVideos(
                        tracks = state.albumTracks,
                        playingId = state.currentTrack.id ?: "",
                        navToContent = {
                            scope.launch {
                                viewModel.playVideo(it.toTrackDtoItem(), Enums.VideoType.Album.name)
                            }
                        },
                        showCount = state.showCountAlbum,
                        showMore = viewModel::showMoreAlbum,
                    )
                }

            if (state.albums.data != null)
                item {
                    LazyRow {
                        items(count = state.albums.data?.size ?: 0) {
                            VideoAlbum(
                                album = state.albums.data?.get(it) ?: AlbumDtoItem(),
                                navToContent = { album ->
                                    viewModel.albumSelected(album)
                                    scope.launch {
                                        viewModel.scrollToTop(scrollState)
                                    }

                                },
                            )
                        }
                    }
                }

            if (!state.downloadedTracks.data.isNullOrEmpty())
                item {
                    Videos(
                        tracks = state.downloadedTracks,
                        navToContent = { tracksDtoItem ->
                            scope.launch {
                                viewModel.playVideo(
                                    tracksDtoItem.copy(contentBaseUrl = ""),
                                    Enums.VideoType.Download.name
                                )
                            }
                        },
                        showCount = state.showCountdownloads,
                        showMore = viewModel::showMoreDownloads,
                        playingId = -1,
                        delete = {
                            deleteTrack.value = it
                            isDelete.value = true
                        }
                    )
                }
        }
    }
}