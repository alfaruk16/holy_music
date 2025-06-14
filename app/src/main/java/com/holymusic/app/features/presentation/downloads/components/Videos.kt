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
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.VideoFile
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.holymusic.app.core.components.NoContent
import com.holymusic.app.core.theme.BackGroundDark
import com.holymusic.app.core.theme.Gray
import com.holymusic.app.core.theme.GrayLight
import com.holymusic.app.core.theme.Orange
import com.holymusic.app.core.theme.OrangeLight
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.core.theme.replaceSize
import com.holymusic.app.core.theme.twoHundredTen
import com.holymusic.app.features.data.remote.model.TracksDto
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.presentation.home.components.ShowMoreButton

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Videos(
    tracks: TracksDto,
    playingId: Int,
    delete: (TracksDtoItem) -> Unit,
    navToContent: (TracksDtoItem) -> Unit,
    showCount: Int,
    showMore: () -> Unit
) {

    val width = LocalConfiguration.current.screenWidthDp - 20

    if (!tracks.data.isNullOrEmpty())
        Column {
            FlowColumn {
                repeat(showCount.coerceAtMost(tracks.data.size)) { index ->
                    val track = tracks.data[index]
                    if (track.id?.toInt() != playingId)
                        Card(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                .clip(shape = RoundedCornerShape(10.dp))
                                .clickable {
                                    navToContent(track)
//                                if (select != null) {
//                                    select(track)
//                                }
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onBackground)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Box(contentAlignment = Alignment.Center) {
                                    AsyncImage(
                                        model = track.contentBaseUrl + replaceSize(
                                            track.imageUrl ?: "",
                                            twoHundredTen
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .width((width / 2).dp)
                                            .height((width / 2 / 16 * 9).dp),
                                        contentScale = ContentScale.FillBounds,
                                        placeholder = rememberVectorPainter(image = Icons.Default.VideoFile),
                                        error = rememberVectorPainter(image = Icons.Default.VideoFile),
                                    )
                                    Icon(
                                        Icons.Filled.PlayCircleOutline,
                                        contentDescription = null,
                                        modifier = Modifier.size(50.dp),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }


                                Spacer(modifier = Modifier.width(15.dp))

                                Column {
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

                                    if (!track.duration.isNullOrEmpty())
                                        Text(
                                            text = (if (track.duration.toInt() / 60 < 10) "0" else "") + (track.duration.toInt()
                                                .div(60)).toString() + " : " + (if (track.duration.toInt() % 60 < 10) "0" else "") + (track.duration.toInt()
                                                .rem(60)).toString(),
                                            color = MaterialTheme.colorScheme.onSecondary,
                                            fontSize = 12.sp,
                                            style = Typography.displaySmall
                                        )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Box(
                                        contentAlignment = Alignment.BottomEnd,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(end = 10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.background(
                                                MaterialTheme.colorScheme.onError,
                                                shape = CircleShape
                                            )
                                        ) {
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
                                    }
                                }
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