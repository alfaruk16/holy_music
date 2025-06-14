package com.holymusic.app.features.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.holymusic.app.R
import com.holymusic.app.core.theme.Gray
import com.holymusic.app.core.theme.GrayLight
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.features.data.remote.model.TracksDtoItem

@Composable
fun AudioTrack(
    content: TracksDtoItem,
    navToContent: (TracksDtoItem) -> Unit,
    imageWidth: Dp
) {

    val count = remember {
        (if (content.playCount != null) content.playCount.toString() else "1")
    }

    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(all = 5.dp)
            .width(imageWidth),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier
            .background(color = MaterialTheme.colorScheme.primaryContainer)
            .clip(shape = RoundedCornerShape(10.dp))
            .clickable { navToContent(content) }) {
            AsyncImage(
                model = content.contentBaseUrl + content.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(imageWidth)
                    .width(imageWidth)
                    .fillMaxSize(),
//                placeholder = painterResource(id = R.drawable.baseline_image_24),
//                error = painterResource(id = R.drawable.baseline_image_24),
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = content.title ?: "",
                    maxLines = 1,
                    modifier = Modifier
                        .padding(start = 10.dp, top = 5.dp, end = 5.dp)
                        .weight(1f),
                    color = MaterialTheme.colorScheme.secondary,
                    style = Typography.displaySmall
                )
                Text(
                    text = "$count " + stringResource(id = R.string.plays),
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    modifier = Modifier.padding(end = 10.dp),
                    style = Typography.displaySmall
                )
            }
            Text(
                text = content.artistName ?: "",
                maxLines = 1,
                modifier = Modifier.padding(start = 10.dp),
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = 12.sp,
                style = Typography.displaySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}