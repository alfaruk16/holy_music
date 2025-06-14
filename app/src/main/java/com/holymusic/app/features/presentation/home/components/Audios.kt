package com.holymusic.app.features.presentation.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.holymusic.app.features.data.remote.model.AlbumDtoItem
import com.holymusic.app.features.data.remote.model.CategoryDtoItem
import com.holymusic.app.features.data.remote.model.TracksDto
import com.holymusic.app.features.data.remote.model.TracksDtoItem

@Composable
fun Audios(
    category: CategoryDtoItem,
    album: AlbumDtoItem,
    tracks: TracksDto,
    navToAlbumItem: (CategoryDtoItem, AlbumDtoItem) -> Unit,
    navToAudioPlayer: (CategoryDtoItem, TracksDtoItem) -> Unit,
    width: Dp
) {
    Column {
        ForwardIcon(
            category = category, navToCategory = {
                navToAlbumItem(category, album)
            }
        )

        LazyRow(contentPadding = PaddingValues(horizontal = 5.dp),
            content = {
                items(count =
                tracks.data?.size ?: 0, itemContent = {
                    val content = tracks.data?.get(it) ?: TracksDtoItem()
                    AudioTrack(content = content, navToContent = { track ->
                        navToAudioPlayer(category, track)
                    }, imageWidth = width)
                })
            })
    }
}