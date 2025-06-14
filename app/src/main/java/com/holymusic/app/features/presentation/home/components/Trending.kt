package com.holymusic.app.features.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.features.data.remote.model.TrackBillboardDtoItem

@Composable
fun Trending(
    track: TrackBillboardDtoItem,
    onTap: (TrackBillboardDtoItem) -> Unit
) {
    Column {

        Card(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .padding(vertical = 5.dp, horizontal = 10.dp)
                .fillMaxWidth()
                .clickable {
                    onTap(track)
                },
            elevation = CardDefaults.cardElevation(5.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary)
        ) {

            AsyncImage(
                model = track.contentBaseUrl + track.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = track.title ?: "",
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                style = Typography.displayMedium
            )
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}