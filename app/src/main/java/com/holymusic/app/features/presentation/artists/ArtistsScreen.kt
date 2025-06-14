package com.holymusic.app.features.presentation.artists

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.holymusic.app.core.components.AppBar
import com.holymusic.app.core.components.BottomPlayerView
import com.holymusic.app.core.components.Loader
import com.holymusic.app.core.theme.BackGroundColor
import com.holymusic.app.features.data.remote.model.ArtistDtoItem
import com.holymusic.app.features.data.remote.model.CategoryDtoItem
import com.holymusic.app.features.presentation.artists.components.Artist

@Composable
fun ArtistsScreen(
    modifier: Modifier = Modifier,
    viewModel: ArtistsViewModel = hiltViewModel(),
    category: CategoryDtoItem? = null,
    navToArtist: (ArtistDtoItem) -> Unit,
    navController: NavController,
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

        Column {

            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .weight(1f)
                    .background(color = MaterialTheme.colorScheme.background),
            ) {

                LazyVerticalGrid(columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(5.dp),
                    content = {
                        items(count = state.artistList.data?.size ?: 0) {
                            Artist(
                                state.artistList.data?.get(it) ?: ArtistDtoItem(),
                                navToContent = { artist ->
                                    navToArtist(artist)
                                },
                            )
                        }
                    })
            }

            BottomPlayerView(
                viewModel.audioExoPlayer,
                navToChoosePlan = navToChoosePlan,
                navToLogin = navToLogin
            )
        }

    }

}
