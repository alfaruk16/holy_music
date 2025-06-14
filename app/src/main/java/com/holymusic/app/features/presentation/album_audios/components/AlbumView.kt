package com.holymusic.app.features.presentation.album_audios.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.holymusic.app.core.theme.Gray
import com.holymusic.app.core.theme.GrayLight
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.core.theme.fourHundred
import com.holymusic.app.core.theme.replaceSize
import com.holymusic.app.core.theme.threeHundred
import com.holymusic.app.features.data.remote.model.AlbumDtoItem

@Composable
fun AlbumView(album: AlbumDtoItem, videoAlbum: Boolean = false) {
    val width = LocalConfiguration.current.screenWidthDp * 5 / 6


    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AsyncImage(
            model = album.contentBaseUrl + replaceSize(
                album.imageUrl ?: "",
                if (videoAlbum) fourHundred else
                    threeHundred
            ),
            contentDescription = null,
            modifier = Modifier
                .height((width / 16 * 9).dp)
                .width(if(videoAlbum)width.dp else (width / 16 * 9).dp)
                .clip(shape = RoundedCornerShape(12.dp)),
            contentScale = ContentScale.FillBounds,
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = album.title ?: "",
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            fontSize = 20.sp,
            style = Typography.displayLarge
        )
        Text(
            text = album.about ?: "",
            color = MaterialTheme.colorScheme.onSecondary,
            maxLines = 1,
            style = Typography.displaySmall
        )
    }
}