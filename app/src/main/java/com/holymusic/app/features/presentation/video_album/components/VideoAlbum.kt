package com.holymusic.app.features.presentation.video_album.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.holymusic.app.R
import com.holymusic.app.core.theme.BackGroundDark
import com.holymusic.app.core.theme.Gray
import com.holymusic.app.core.theme.GrayLight
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.core.theme.replaceSize
import com.holymusic.app.core.theme.twelveHundred
import com.holymusic.app.features.data.remote.model.AlbumDtoItem

@Composable
fun VideoAlbum(
    album: AlbumDtoItem,
    navToContent: (AlbumDtoItem) -> Unit
) {
    val width = LocalConfiguration.current.screenWidthDp * 5 / 6

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            Modifier
                .padding(5.dp),
            shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onBackground),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                Modifier
                    .width((width).dp)
                    .clip(shape = RoundedCornerShape(15.dp))
                    .clickable {
                        navToContent(album)
                    },
            ) {

                AsyncImage(
                    model = album.contentBaseUrl + replaceSize(
                        album.imageUrl ?: "",
                        twelveHundred
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .height((width / 16 * 9).dp)
                        .width(width.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.baseline_image_24),
                    error = painterResource(id = R.drawable.baseline_image_24)
                )
                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = album.about.toString(),
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    modifier = Modifier.padding(start = 15.dp),
                    style = Typography.displayMedium
                )
                Text(
                    text = album.totalTrack.toString() + " " + stringResource(id = R.string.gajal),
                    color = MaterialTheme.colorScheme.onSecondary,
                    maxLines = 1,
                    modifier = Modifier.padding(start = 15.dp),
                    style = Typography.displaySmall
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

        }
    }
}