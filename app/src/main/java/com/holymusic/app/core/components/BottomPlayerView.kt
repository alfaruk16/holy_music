package com.holymusic.app.core.components

import android.annotation.SuppressLint
import android.app.Activity
import android.support.v4.media.MediaMetadataCompat
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOneOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.holymusic.app.MainActivity
import com.holymusic.app.R
import com.holymusic.app.core.exoplayer.data.entities.Song
import com.holymusic.app.core.exoplayer.isPlaying
import com.holymusic.app.core.exoplayer.toSong
import com.holymusic.app.core.exoplayer.viewmodels.AudioExoPlayer
import com.holymusic.app.core.theme.Primary
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.core.util.AppConstants
import com.holymusic.app.core.util.Common
import com.holymusic.app.features.data.local.model.FileItem
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.domain.services.Share
import com.holymusic.app.features.presentation.Screens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@SuppressLint("UnrememberedMutableInteractionSource", "ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomPlayerView(
    audioExoPlayer: AudioExoPlayer,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navToChoosePlan: () -> Unit,
    navToLogin: () -> Unit,
    updateFavorite: ((Boolean, String) -> Unit)? = null,
    updateDownload: (() -> Unit)? = null,
    hasFavorite: Boolean? = null
) {

    val track = audioExoPlayer.curPlayingSong.observeAsState().value
    val playbackState = audioExoPlayer.playbackState.observeAsState().value
    val played = audioExoPlayer.played.observeAsState().value
    val currentPlayingPosition = audioExoPlayer.curPlayerPosition.observeAsState().value
    val currentSongDuration = audioExoPlayer.curSongDuration.observeAsState().value
    val isFavorite = audioExoPlayer.isFavorite.observeAsState()
    val downloadProgress = audioExoPlayer.downloadProgress.observeAsState().value
    var offsetX by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current.density
    val width = LocalConfiguration.current.screenWidthDp
    val context = LocalContext.current
    val activity = LocalContext.current as Activity

    val modalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val isBottomSheet = remember {
        mutableStateOf(false)
    }

    if (track?.toSong()?.title == null || track.toSong()?.title.toString() == "null") {
        audioExoPlayer.close()
        isBottomSheet.value = false
        if (updateFavorite != null && isFavorite.value != null) {
            updateFavorite(isFavorite.value ?: false, track?.toSong()?.mediaId ?: "")
        }
        if (updateDownload != null) {
            updateDownload()
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            audioExoPlayer.updateCurrentPlayerPosition()
            delay(1000)
        }
    }

    if (isBottomSheet.value)
        ModalBottomSheet(
            onDismissRequest = {
                isBottomSheet.value = false
                if (updateFavorite != null && isFavorite.value != null) {
                    updateFavorite(isFavorite.value ?: false, track?.toSong()?.mediaId ?: "")
                }
                if (updateDownload != null) {
                    updateDownload()
                }
            },
            modifier = Modifier
                .fillMaxSize(),
            sheetState = modalBottomSheetState,
            dragHandle = {},
        ) {
            Column(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Common.getImageColor(track?.toSong()?.mediaId),
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
                    .weight(1f)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(start = 20.dp, end = 20.dp, top = 30.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        Icons.Filled.ArrowBackIosNew, contentDescription = null,
                        modifier = Modifier
                            .rotate(-90f)
                            .clip(shape = CircleShape)
                            .clickable {
                                coroutineScope.launch {
                                    modalBottomSheetState
                                        .hide()
                                        .apply {
                                            isBottomSheet.value = false
                                            if (updateFavorite != null && isFavorite.value != null) {
                                                updateFavorite(
                                                    isFavorite.value ?: false,
                                                    track?.toSong()?.mediaId ?: ""
                                                )
                                            }
                                            if (updateDownload != null) {
                                                updateDownload()
                                            }
                                        }
                                }
                            },
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(
                        text = stringResource(id = R.string.now_playing),
                        fontSize = 20.sp, fontWeight = FontWeight.W700,
                        color = MaterialTheme.colorScheme.secondary,
                        style = Typography.displayLarge
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Filled.Share, contentDescription = null,
                        modifier = Modifier.clickable {
                            Share.shareData(
                                context = context,
                                title = track?.toSong()?.title ?: "",
                                screen = Screens.ARTIST_AUDIO_PLAYER_SCREEN,
                                id = track?.toSong()?.mediaId ?: ""
                            )
                        }, tint = MaterialTheme.colorScheme.secondary)
                }

                LazyColumn(modifier = Modifier
                    .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = {
                        item {
                            AsyncImage(
                                model = track?.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI)
                                    ?.replace("72", "300"),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((width - 80).dp)
                                    .padding(top = 20.dp, start = 20.dp, end = 20.dp)
                                    .clip(shape = RoundedCornerShape(15.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        item {
                            Text(
                                text = track?.toSong()?.title ?: "",
                                maxLines = 1,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(top = 20.dp, bottom = 5.dp),
                                style = Typography.displayLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        item {
                            Text(
                                text = track?.toSong()?.subtitle ?: "",
                                color = MaterialTheme.colorScheme.onSecondary,
                                style = Typography.displaySmall
                            )
                        }

                        item { Spacer(modifier = Modifier.height(30.dp)) }

                        item {

                            Row(
                                modifier = Modifier
                                    .padding(vertical = 30.dp, horizontal = 40.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.QueueMusic,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                                Icon(
                                    if (isFavorite.value == true) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = null,
                                    tint = if (isFavorite.value == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier
                                        .clip(shape = CircleShape)
                                        .clickable {
                                            if (MainActivity.isLoggedIn) {
                                                audioExoPlayer.setFavorite(TracksDtoItem(id = track?.toSong()?.mediaId))
                                            } else {
                                                navToLogin()
                                            }
                                        }
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(shape = CircleShape)
                                        .clip(shape = CircleShape)
                                        .clickable {
                                            if (MainActivity.isPremium.value) {
                                                val second = currentSongDuration?.div(1000) ?: 0
                                                audioExoPlayer.download(
                                                    activity,
                                                    FileItem(
                                                        id = track?.toSong()?.mediaId ?: "",
                                                        url = track?.toSong()?.songUrl ?: "",
                                                        name = track?.toSong()?.title ?: "",
                                                        mimeType = AppConstants.typeAudio,
                                                        contentBaseUrl = "",
                                                        imageUrl = track?.toSong()?.imageUrl ?: "",
                                                        artistName = track?.toSong()?.subtitle
                                                            ?: "",
                                                        description = "",
                                                        duration = second.toString()
                                                    )
                                                )
                                            } else {
                                                navToChoosePlan()
                                            }
                                        }
                                        .size(30.dp),
                                    contentAlignment = Alignment.Center
                                ) {

                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        if (downloadProgress != null) {
                                            drawArc(
                                                color = Primary,
                                                -90f,
                                                downloadProgress.toFloat() /
                                                        100 * 360f,
                                                useCenter = false,
                                                style = Stroke(15f, cap = StrokeCap.Round)
                                            )
                                        }
                                    }

                                    Icon(
                                        Icons.Filled.Download,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                if (currentPlayingPosition != null && currentSongDuration != null) {
                                    Slider(
                                        value = (currentPlayingPosition.div(currentSongDuration.toFloat())),
                                        onValueChange = {
                                            audioExoPlayer.seekTo((it * currentSongDuration).toLong())
                                        },
                                        colors = SliderDefaults.colors(
                                            activeTrackColor = MaterialTheme.colorScheme.primary,
                                        ),
                                        modifier = Modifier
                                            .height(12.dp)
                                            .padding(horizontal = 5.dp)
                                            .scale(1f, .33f),
                                        thumb = {
                                            SliderDefaults.Thumb( //androidx.compose.material3.SliderDefaults
                                                interactionSource = MutableInteractionSource(),
                                                thumbSize = DpSize(10.dp, 10.dp),
                                                modifier = Modifier.scale(1f, 3f),
                                                colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary)
                                            )
                                        },
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp)
                                ) {
                                    if (currentPlayingPosition != null) {
                                        val second = currentPlayingPosition / 1000
                                        Text(
                                            text = (if (second / 60 < 10) "0" else "") + (second / 60).toString() + " : " + (if (second % 60 < 10) "0" else "") + (second % 60).toString(),
                                            color = MaterialTheme.colorScheme.onSecondary,
                                            style = Typography.displaySmall
                                        )
                                    }
                                    if (currentSongDuration != null) {
                                        val second = currentSongDuration / 1000
                                        Text(
                                            text = (if (second / 60 < 10) "0" else "") + (second / 60).toString() + " : " + (if (second % 60 < 10) "0" else "") + (second % 60).toString(),
                                            color = MaterialTheme.colorScheme.onSecondary,
                                            style = Typography.displaySmall
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 40.dp, vertical = 10.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {

                                    if (audioExoPlayer.isShuffle.value == true)
                                        Icon(
                                            Icons.Filled.ShuffleOn,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .clip(shape = CircleShape)
                                                .clickable { audioExoPlayer.disableShuffle() }
                                        ) else
                                        Icon(
                                            Icons.Filled.Shuffle,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier
                                                .clip(shape = CircleShape)
                                                .clickable { audioExoPlayer.enableShuffle() }
                                        )

                                    Icon(
                                        Icons.Filled.SkipPrevious,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary, modifier =
                                        Modifier
                                            .size(40.dp)
                                            .clip(shape = CircleShape)
                                            .clickable {
                                                if (MainActivity.isPremium.value) {
                                                    audioExoPlayer.skipToPreviousSong()
                                                } else {
                                                    navToChoosePlan()
                                                }
                                            }
                                    )

                                    Icon(
                                        if (playbackState?.isPlaying == true) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier
                                            .size(45.dp)
                                            .clip(shape = CircleShape)
                                            .clickable {
                                                audioExoPlayer.playOrToggleSong(
                                                    track?.toSong() ?: Song(),
                                                    true
                                                )
                                            })

                                    Icon(
                                        Icons.Filled.SkipNext,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary, modifier =
                                        Modifier
                                            .size(40.dp)
                                            .clip(shape = CircleShape)
                                            .clickable {
                                                if (MainActivity.isPremium.value) {
                                                    audioExoPlayer.skipToNextSong()
                                                } else {
                                                    navToChoosePlan()
                                                }
                                            }
                                    )
                                    if (audioExoPlayer.isRepeat.value == true)
                                        Icon(
                                            Icons.Filled.RepeatOneOn,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .clip(shape = CircleShape)
                                                .clickable {
                                                    audioExoPlayer.disableRepeatMode()
                                                }
                                        )
                                    else
                                        Icon(
                                            Icons.Filled.Repeat,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier
                                                .clip(shape = CircleShape)
                                                .clickable {
                                                    audioExoPlayer.enableRepeatMode()
                                                }
                                        )
                                }

                                Spacer(modifier = Modifier.navigationBarsPadding())
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    })
            }
        }

    if (played == true && track != null && playbackState != null)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(bottom = 2.dp)
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .testTag("player")
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta -> offsetX += delta },
                    onDragStopped = {
                        val dp = offsetX / density
                        if (abs(dp) > width / 2) {
                            audioExoPlayer.addPlayCount()
                            audioExoPlayer.close()
                        } else {
                            offsetX = 0f
                        }

                    }
                ),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier
                    .clip(
                        shape = RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp
                        )
                    )
                    .clickable {
                        coroutineScope.launch {
                            isBottomSheet.value = true
                            if (hasFavorite != null) {
                                audioExoPlayer.updateFavorite(hasFavorite)
                            }
                            audioExoPlayer.checkIsDownloaded(track.toSong()?.mediaId ?: "")
                            modalBottomSheetState.expand()
                        }
                    }) {
                if (currentPlayingPosition != null && currentSongDuration != null) {
                    Slider(
                        value = (currentPlayingPosition.div(currentSongDuration.toFloat())),
                        onValueChange = {
                            audioExoPlayer.seekTo((it * currentSongDuration).toLong())
                        },
                        colors = SliderDefaults.colors(
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                        ),
                        modifier = Modifier
                            .height(12.dp)
                            .padding(horizontal = 5.dp)
                            .scale(1f, .33f),
                        thumb = {
                            SliderDefaults.Thumb(
                                interactionSource = MutableInteractionSource(),
                                thumbSize = DpSize(10.dp, 10.dp),
                                modifier = Modifier.scale(1f, 3f),
                                colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary)
                            )
                        },
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 25.dp, end = 20.dp)
                ) {

                    AsyncImage(
                        model = track.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(shape = RoundedCornerShape(8.dp))
                    )

                    Spacer(modifier = Modifier.width(10.dp))


                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.toSong()?.title ?: "",
                            maxLines = 1,
                            style = Typography.displaySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        if (currentPlayingPosition != null) {
                            val second = currentPlayingPosition / 1000
                            Text(
                                text = (if (second / 60 < 10) "0" else "") + (second / 60).toString() + " : " + (if (second % 60 < 10) "0" else "") + (second % 60).toString(),
                                color = MaterialTheme.colorScheme.onSecondary,
                                style = Typography.displaySmall
                            )
                        }
                    }

                    Icon(
                        Icons.Filled.SkipPrevious,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary, modifier =
                        Modifier
                            .size(40.dp)
                            .clip(shape = CircleShape)
                            .clickable {
                                if (MainActivity.isPremium.value) {
                                    audioExoPlayer.skipToPreviousSong()
                                } else {
                                    navToChoosePlan()
                                }
                            }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        if (playbackState.isPlaying) Icons.Filled.PauseCircle else Icons.Filled.PlayCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(45.dp)
                            .clip(shape = CircleShape)
                            .clickable {
                                audioExoPlayer.playOrToggleSong(
                                    track.toSong() ?: Song(),
                                    true
                                )
                            })
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        Icons.Filled.SkipNext,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary, modifier =
                        Modifier
                            .size(40.dp)
                            .clip(shape = CircleShape)
                            .clickable {
                                if (MainActivity.isPremium.value) {
                                    audioExoPlayer.skipToNextSong()
                                } else {
                                    navToChoosePlan()
                                }
                            }
                    )
                }
            }

        }
    else
        offsetX = 0f

}