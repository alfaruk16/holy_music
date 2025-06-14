package com.holymusic.app.features.presentation.album_audios

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.holymusic.app.MainActivity
import com.holymusic.app.core.components.AppBar
import com.holymusic.app.core.components.BottomPlayerView
import com.holymusic.app.core.components.Loader
import com.holymusic.app.core.theme.BackGroundColor
import com.holymusic.app.features.data.remote.model.AlbumDtoItem
import com.holymusic.app.features.data.remote.model.AlbumTrackDtoItem
import com.holymusic.app.features.data.remote.model.CategoryDtoItem
import com.holymusic.app.features.presentation.album_audios.components.AlbumAudios
import com.holymusic.app.features.presentation.album_audios.components.AlbumView
import com.holymusic.app.features.presentation.audio_album.components.AudioAlbum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AlbumAudiosScreen(
    modifier: Modifier = Modifier,
    viewModel: AlbumAudiosViewModel = hiltViewModel(),
    category: CategoryDtoItem? = null,
    navToContent: (AlbumTrackDtoItem) -> Unit,
    navController: NavController,
    scrollState: LazyGridState = rememberLazyGridState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    navToChoosePlan: () -> Unit,
    navToLogin: () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppBar(
                title = category?.name ?: "",
                icon = category?.icon,
                navController = navController
            )
        }
    ) { paddingValues ->

        val state by viewModel.state.collectAsStateWithLifecycle()

        if (state.isLoading) {
            Loader(paddingValues)
        }

        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(color = MaterialTheme.colorScheme.background),
            ) {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3), state = scrollState,
                    contentPadding = PaddingValues(horizontal = 5.dp)
                ) {

                    item(span = { GridItemSpan(3) }) {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    item(span = { GridItemSpan(3) }) {
                        AlbumView(album = state.currentAlbum)
                    }
                    item(span = { GridItemSpan(3) }) {
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                    item(span = { GridItemSpan(3) }) {
                        AlbumAudios(
                            tracks = state.tracks,
                            play = viewModel::playAudio,
                            navToContent = navToContent,
                            showCount = state.showCount,
                            showMore = viewModel::showMore,
                            audioExoplayer = viewModel.audioExoPlayer,
                            navToChoosePlan = navToChoosePlan
                        )
                    }
                    item(span = { GridItemSpan(3) }) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (state.albumList.data != null)
                        item(span = { GridItemSpan(3) }) {
                            LazyRow {
                                items(count = state.albumList.data?.size ?: 0) {
                                    AudioAlbum(
                                        state.albumList.data?.get(it) ?: AlbumDtoItem(),
                                        navToContent = { album ->
                                                viewModel.albumSelected(album = album)
                                                scope.launch {
                                                    viewModel.scrollToTop(0, scrollState)
                                                }
                                        }
                                    )
                                }
                            }
                        }
                    item(span = { GridItemSpan(3) }) {
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }
            }

            BottomPlayerView(
                viewModel.audioExoPlayer,
                navToChoosePlan = navToChoosePlan,
                navToLogin = navToLogin
            )
        }

        LaunchedEffect(Unit) {
            viewModel.subscribeToObservers()
        }
    }

}
