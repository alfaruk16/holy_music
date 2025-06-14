package com.holymusic.app.features.presentation.audio_album.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.holymusic.app.R
import com.holymusic.app.core.theme.BackGroundDark
import com.holymusic.app.core.theme.Gray
import com.holymusic.app.core.theme.GrayLight
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.core.theme.hundredNinetyTwo
import com.holymusic.app.core.theme.replaceSize
import com.holymusic.app.features.data.remote.model.AlbumDtoItem

@Composable
fun AudioAlbum(
    album: AlbumDtoItem,
    navToContent: (AlbumDtoItem) -> Unit
) {
    val width = LocalConfiguration.current.screenWidthDp

    Card(
        Modifier
            .padding(5.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onBackground),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            Modifier
                .width((width / 3).dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .clickable {
                    navToContent(album)
                },
        ) {

            AsyncImage(
                model = album.contentBaseUrl + replaceSize(
                    album.imageUrl ?: "",
                    hundredNinetyTwo
                ),
                contentDescription = null,
                modifier = Modifier
                    .height((width / 3).dp)
                    .width((width / 3).dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.baseline_image_24),
                error = painterResource(id = R.drawable.baseline_image_24)
            )
            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = album.title.toString(),
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                modifier = Modifier.padding(start = 8.dp),
                style = Typography.displaySmall
            )
            Text(
                text = album.totalTrack.toString() + " " + stringResource(id = R.string.gajal),
                color = MaterialTheme.colorScheme.onSecondary,
                maxLines = 1,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp),
                style = Typography.displaySmall
            )
            Spacer(modifier = Modifier.height(5.dp))
        }

    }
}