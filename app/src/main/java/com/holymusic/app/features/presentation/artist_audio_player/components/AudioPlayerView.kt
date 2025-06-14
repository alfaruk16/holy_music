package com.holymusic.app.features.presentation.artist_audio_player.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.holymusic.app.MainActivity
import com.holymusic.app.R
import com.holymusic.app.core.exoplayer.isPlaying
import com.holymusic.app.core.exoplayer.toSong
import com.holymusic.app.core.exoplayer.viewmodels.AudioExoPlayer
import com.holymusic.app.core.theme.Primary
import com.holymusic.app.core.theme.replaceSize
import com.holymusic.app.core.theme.threeHundred
import com.holymusic.app.core.util.AppConstants
import com.holymusic.app.features.data.local.model.FileItem
import com.holymusic.app.features.data.remote.model.TracksDtoItem

@Composable
fun AudioPlayerView(
    track: TracksDtoItem,
    playAudio: (TracksDtoItem) -> Unit,
    audioExoPlayer: AudioExoPlayer,
    setFavorite: (TracksDtoItem) -> Unit,
    isFavorite: Boolean,
    download: (FileItem) -> Unit,
    downloadProgress: Int = 0,
    navToChoosePlan: () -> Unit
) {

    val currentPlayingSong = audioExoPlayer.curPlayingSong.observeAsState().value
    val playbackState = audioExoPlayer.playbackState.observeAsState().value
    val width = LocalConfiguration.current.screenWidthDp

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            AsyncImage(
                model = track.contentBaseUrl + replaceSize(
                    track.imageUrl ?: "",
                    threeHundred
                ),
                contentDescription = null,
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(15.dp))
                    .width((width / 1.5).dp)
                    .height((width / 1.5).dp),
                contentScale = ContentScale.FillBounds
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = track.title ?: "", fontSize = 20.sp, color = MaterialTheme.colorScheme.secondary)

            Spacer(modifier = Modifier.height(5.dp))

            Text(text = track.artistName ?: "", color = MaterialTheme.colorScheme.onSecondary)

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.width((width / 1.5).dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                            .align(Alignment.CenterHorizontally)
                            .clip(shape = CircleShape)
                            .clickable {
                                setFavorite(track)
                            }
                            .size(45.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = stringResource(id = R.string.like),
                        color = MaterialTheme.colorScheme.secondary)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                            .align(Alignment.CenterHorizontally)
                            .clip(shape = CircleShape)
                            .clickable {
                                if (MainActivity.isPremium.value || (track.isPremium != true)) {
                                    playAudio(track)
                                } else {
                                    navToChoosePlan()
                                }
                            }
                            .size(45.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (currentPlayingSong != null && currentPlayingSong.toSong()?.mediaId == track.id && playbackState != null && playbackState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = null, tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = if (currentPlayingSong != null && currentPlayingSong.toSong()?.mediaId == track.id && playbackState != null && playbackState.isPlaying) R.string.pause else R.string.play),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Column {
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
                            .align(Alignment.CenterHorizontally)
                            .clip(shape = CircleShape)
                            .clickable {
                                download(
                                    FileItem(
                                        id = track.id ?: "",
                                        url = track.contentBaseUrl + track.streamUrl,
                                        name = track.title ?: "",
                                        mimeType = AppConstants.typeAudio,
                                        contentBaseUrl = track.contentBaseUrl ?: "",
                                        imageUrl = track.imageUrl ?: "",
                                        artistName = track.artistName ?: "",
                                        description = track.about ?: "",
                                        duration = track.duration ?: ""
                                    )
                                )
                            }
                            .size(45.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawArc(
                                color = Primary,
                                -90f,
                                downloadProgress.toFloat() / 100 * 360f,
                                useCenter = false,
                                style = Stroke(15f, cap = StrokeCap.Round)
                            )
                        }

                        Icon(
                            Icons.Filled.Download,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = stringResource(id = R.string.download),
                        color = MaterialTheme.colorScheme.secondary)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}