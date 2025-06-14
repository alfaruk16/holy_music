package com.holymusic.app.features.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.holymusic.app.MainActivity
import com.holymusic.app.R
import com.holymusic.app.core.components.AudioVideoTab
import com.holymusic.app.core.components.BottomPlayerView
import com.holymusic.app.core.components.Loader
import com.holymusic.app.core.components.NoInternet
import com.holymusic.app.core.components.TextFieldK
import com.holymusic.app.core.components.video_player.VideoPlayerView
import com.holymusic.app.core.theme.BackGroundColor
import com.holymusic.app.core.theme.BackGroundDark
import com.holymusic.app.core.theme.Gray
import com.holymusic.app.core.theme.Primary
import com.holymusic.app.core.theme.TextFieldBackGround
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.core.util.AppConstants
import com.holymusic.app.core.util.Enums
import com.holymusic.app.core.util.KeyboardUnFocusHandler
import com.holymusic.app.core.util.isOnline
import com.holymusic.app.features.data.remote.model.ArtistDtoItem
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.presentation.Screens
import com.holymusic.app.features.presentation.artist_audios.components.ArtistAudios
import com.holymusic.app.features.presentation.audio_artists.components.AudioArtists
import com.holymusic.app.features.presentation.video_artists.components.ArtistVideos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    navToArtist: (ArtistDtoItem, String) -> Unit,
    navToTrack: (TracksDtoItem) -> Unit,
    navToChoosePlan: () -> Unit,
    navToLogin: () -> Unit,
    navToVideoPlayer: (TracksDtoItem) -> Unit,
    navController: NavController,
    scope: CoroutineScope = rememberCoroutineScope()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val searchFocusRequester = FocusRequester()
    val context = LocalContext.current
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true
        )
    )
    BottomSheetScaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        sheetDragHandle = {},
        sheetMaxWidth = Dp.Infinity,
        sheetContent = {
            if (!state.currentTrack.streamUrl.isNullOrEmpty())
                VideoPlayerView(
                    track = state.currentTrack,
                    navController = navController,
                    sheetState = scaffoldState.bottomSheetState,
                    close = { viewModel.closeMiniPlayer() },
                    navToLogin = navToLogin,
                    navToChoosePlan = navToChoosePlan,
                    audioPlayer = viewModel.audioExoPlayer,
                    videoType = Enums.VideoType.Artist.name
                )
        },
        scaffoldState = scaffoldState,
        sheetPeekHeight = if (!state.currentTrack.streamUrl.isNullOrEmpty()) 60.dp else 0.dp,
        sheetShape = RectangleShape
    ) { paddingValues ->


        KeyboardUnFocusHandler()

        LaunchedEffect(Unit) {
            if (state.isLoading) {
                viewModel.init()
            }
        }

        if (!isOnline(context))
            Box(modifier = Modifier.padding(paddingValues)) {
                NoInternet {
                    if (isOnline(context)) {
                        navController.navigate(Screens.SEARCH_SCREEN) {
                            popUpTo(Screens.HOME_SCREEN)
                        }
                    }
                }
            }
        else
            if (state.isLoading) {
                Loader(paddingValues)
            } else

                Column(modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize()) {
                    Spacer(
                        modifier = Modifier
                            .height(5.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 5.dp)
                    ) {
                        Box(modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .clip(shape = CircleShape)
                            .clickable {
                                navController.navigateUp()
                            }
                            .background(MaterialTheme.colorScheme.onBackground, shape = CircleShape)) {
                            Icon(
                                Icons.Filled.ArrowBackIosNew, contentDescription = null,
                                modifier = Modifier.padding(5.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        TextFieldK(
                            value = state.search,
                            onValueChange = { viewModel.searchChanged(it, focusManager) },
                            focusRequester = searchFocusRequester,
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            label = R.string.search,
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .weight(1f),
                            height = 40.dp,
                            containerColor = MaterialTheme.colorScheme.surfaceDim,
                            cornerRadius = 20,
                            borderColor = Color.Transparent,
                            placeHolderFontSize = 14
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                    ) {

                        item {
                            Spacer(modifier = Modifier.height(5.dp))
                        }

                        item {
                            AudioVideoTab(selectedTab = state.selectedTab) {
                                viewModel.tabChanged(it)
                            }
                        }

                        item {
                            when (state.selectedTab) {
                                0 -> ArtistAudios(
                                    tracks = state.searchedTracks,
                                    play = viewModel::playAudio,
                                    navToContent = navToTrack,
                                    showCount = state.showCount,
                                    showMore = viewModel::showMore,
                                    audioExoPlayer = viewModel.audioExoPlayer,
                                    navToChoosePlan = navToChoosePlan
                                )

                                1 -> ArtistVideos(
                                    tracks = state.searchedVideoTracks,
                                    navToContent = {
                                        if (MainActivity.isPremium.value || (it.isPremium != true)) {
                                            scope.launch {
                                                viewModel.playVideo(it)
                                                delay(100)
                                                scaffoldState.bottomSheetState.expand()
                                            }
                                        } else {
                                            navToChoosePlan()
                                        }
                                    },
                                    showCount = state.showCountVideo,
                                    showMore = viewModel::showMore,
                                    playingId = ""
                                )
                            }
                        }
                        item { Spacer(modifier = Modifier.height(10.dp)) }

                        if (!state.searchedArtist.data.isNullOrEmpty())
                            item {
                                Text(
                                    text = stringResource(id = R.string.popular_artist),
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(start = 15.dp),
                                    style = Typography.titleLarge
                                )
                            }
                        item { Spacer(modifier = Modifier.height(10.dp)) }
                        item {
                            AudioArtists(state.searchedArtist, {
                                navToArtist(
                                    it,
                                    if (state.selectedTab == 0) AppConstants.typeAudio else AppConstants.typeVideo
                                )
                            })
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
