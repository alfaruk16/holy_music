package com.holymusic.app.features.presentation.downloads.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.holymusic.app.R
import com.holymusic.app.core.components.NoContent
import com.holymusic.app.core.theme.BackGroundDark
import com.holymusic.app.core.theme.Gray
import com.holymusic.app.core.theme.GrayLight
import com.holymusic.app.core.theme.OrangeLight
import com.holymusic.app.core.theme.Primary
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.core.theme.replaceSize
import com.holymusic.app.core.theme.seventyTwo
import com.holymusic.app.features.data.remote.model.TracksDto
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.presentation.home.components.ShowMoreButton

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Audios(
    tracks: TracksDto,
    play: (TracksDtoItem) -> Unit,
    delete: (TracksDtoItem) -> Unit,
    navToContent: (TracksDtoItem) -> Unit,
    showCount: Int,
    showMore: () -> Unit,
    select: ((TracksDtoItem) -> Unit?)? = null,
    playingId: String
) {

    if (!tracks.data.isNullOrEmpty())
        Column {
            FlowColumn {
                repeat(showCount.coerceAtMost(tracks.data.size)) { index ->
                    val track = tracks.data[index]
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .clip(shape = RoundedCornerShape(10.dp))
                            .clickable {
                                navToContent(track)
                                if (select != null) {
                                    select(track)
                                }
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onBackground)
                    ) {
                        Row(
                            Modifier.padding(vertical = 10.dp, horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            AsyncImage(
                                model = track.contentBaseUrl + replaceSize(
                                    track.imageUrl ?: "",
                                    seventyTwo
                                ),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(shape = RoundedCornerShape(10)),
                                contentScale = ContentScale.FillHeight,
                                placeholder = rememberVectorPainter(image = Icons.Default.MusicNote),
                                error = rememberVectorPainter(image = Icons.Default.MusicNote),
                            )
                            Spacer(modifier = Modifier.width(15.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = track.title ?: "",
                                    color = MaterialTheme.colorScheme.secondary,
                                    maxLines = 1,
                                    style = Typography.displaySmall
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = track.artistName ?: "",
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    style = Typography.displaySmall
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                if (track.playCount != null)
                                    Text(
                                        text = track.playCount.toString() + " " + stringResource(id = R.string.plays),
                                        color = MaterialTheme.colorScheme.onSecondary,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        style = Typography.displaySmall
                                    )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            if (track.duration != null)
                                Text(
                                    text = (if (track.duration.toInt() / 60 < 10) "0" else "") + (track.duration.toInt()
                                        .div(60)).toString() + " : " + (if (track.duration.toInt() % 60 < 10) "0" else "") + (track.duration.toInt()
                                        .rem(60)).toString(),
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    fontSize = 12.sp,
                                    style = Typography.displaySmall
                                )
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(modifier = Modifier.background(MaterialTheme.colorScheme.onError, shape = CircleShape)) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(shape = CircleShape)
                                        .clickable { delete(track) }
                                        .padding(2.dp),
                                    tint = MaterialTheme.colorScheme.primaryContainer
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(
                                if (track.id == playingId) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(shape = CircleShape)
                                    .clickable { play(track) },
                                tint = Primary
                            )

                        }
                    }
                }
                if (tracks.data.size > 5)
                    ShowMoreButton(isShowMore = showCount < tracks.data.size, showMore)
            }
        } else if (tracks.status != null) {
        NoContent()
    }

}