package com.holymusic.app.features.presentation.video_scholars.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.holymusic.app.R
import com.holymusic.app.core.theme.BackGroundDark
import com.holymusic.app.core.theme.Gray
import com.holymusic.app.core.theme.GrayLight
import com.holymusic.app.core.theme.hundredNinetyTwo
import com.holymusic.app.core.theme.replaceSize
import com.holymusic.app.features.data.remote.model.ArtistDto
import com.holymusic.app.features.data.remote.model.ArtistDtoItem

@Composable
fun ScholarArtist(
    artistList: ArtistDto,
    navToContent: (ArtistDtoItem) -> Unit,
    currentArtistId: String = ""
) {
    val width = LocalConfiguration.current.screenWidthDp

    LazyRow(contentPadding = PaddingValues(horizontal = 5.dp)) {
        items(count = artistList.data?.size ?: 0,
            key = { it },
            itemContent = { index ->
                val artist = artistList.data?.get(index)
                if (artist?.id != currentArtistId)
                    Card(
                        Modifier
                            .padding(5.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = BackGroundDark),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Box(modifier = Modifier
                            .clip(shape = RoundedCornerShape(10.dp))
                            .clickable {
                                navToContent(artist ?: ArtistDtoItem())
                            }) {
                            Column(
                                Modifier
                                    .padding(vertical = 10.dp, horizontal = 15.dp)
                                    .width((width / 3).dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                AsyncImage(
                                    model = artist?.contentBaseUrl + replaceSize(
                                        artist?.imageUrl ?: "",
                                        hundredNinetyTwo
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height((width / 3 - 10).dp)
                                        .width((width / 3).dp)
                                        .padding(horizontal = 5.dp)
                                        .clip(shape = CircleShape),
                                    contentScale = ContentScale.FillBounds,
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = artist?.name ?: "",
                                    color = Gray,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                                Text(
                                    text = (if (artist?.totalTrack != null) artist.totalTrack.toString() else "0") + " " + stringResource(
                                        id = R.string.contents
                                    ) + if ((artist?.totalTrack ?: 0) > 1) "s" else "",
                                    color = GrayLight,
                                    maxLines = 1,
                                    fontSize = 12.sp
                                )

                            }
                        }
                    }
            })
    }
}