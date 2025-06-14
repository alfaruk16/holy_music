package com.holymusic.app.features.presentation.audio_artists.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.core.theme.hundredNinetyTwo
import com.holymusic.app.core.theme.replaceSize
import com.holymusic.app.features.data.remote.model.ArtistDto
import com.holymusic.app.features.data.remote.model.ArtistDtoItem

@Composable
fun AudioArtists(
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
                        Modifier.padding(5.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onBackground),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Box(modifier = Modifier
                            .clip(shape = RoundedCornerShape(15.dp))
                            .clickable {
                                navToContent(artist ?: ArtistDtoItem())
                            }) {
                            Column(
                                Modifier
                                    .padding(bottom = 10.dp)
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
                                        .clip(shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),
                                    contentScale = ContentScale.FillBounds,
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = artist?.name ?: "",
                                    color = MaterialTheme.colorScheme.secondary,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    style = Typography.displaySmall
                                )
                                Text(
                                    text = (if (artist?.totalTrack != null) artist.totalTrack.toString() else "0") + " " + stringResource(
                                        id = R.string.gajal
                                    ),
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    maxLines = 1,
                                    fontSize = 12.sp,
                                    style = Typography.displaySmall
                                )

                            }
                        }
                    }
            })
    }
}