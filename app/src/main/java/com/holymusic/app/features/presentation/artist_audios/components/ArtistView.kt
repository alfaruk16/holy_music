package com.holymusic.app.features.presentation.artist_audios.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import com.holymusic.app.core.theme.replaceSize
import com.holymusic.app.core.theme.threeHundred
import com.holymusic.app.features.data.remote.model.ArtistDtoItem

@Composable
fun ArtistView(artist: ArtistDtoItem) {
    val width = LocalConfiguration.current.screenWidthDp

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AsyncImage(
            model = artist.contentBaseUrl + replaceSize(
                artist.imageUrl ?: "",
                threeHundred
            ),
            contentDescription = null,
            modifier = Modifier
                .height((width / 2 - 10).dp)
                .width((width / 2).dp)
                .padding(horizontal = 5.dp)
                .clip(shape = CircleShape),
            contentScale = ContentScale.FillBounds,
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = artist.name ?: "",
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            fontSize = 20.sp,
            style = Typography.displayLarge
        )
        Text(
            text = artist.about ?: "",
            color = MaterialTheme.colorScheme.onSecondary,
            textAlign = TextAlign.Center,
            style = Typography.displaySmall
        )
    }
}