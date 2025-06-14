package com.holymusic.app.features.presentation.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Switch
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.holymusic.app.R
import com.holymusic.app.core.components.ActionItem
import com.holymusic.app.core.components.AppBar
import com.holymusic.app.core.components.BottomPlayerView
import com.holymusic.app.core.components.Drawer
import com.holymusic.app.core.components.Loader
import com.holymusic.app.core.components.NoInternet
import com.holymusic.app.core.components.PageIndicatorView
import com.holymusic.app.core.components.video_player.VideoPlayerView
import com.holymusic.app.core.util.AppConstants
import com.holymusic.app.core.util.Enums
import com.holymusic.app.core.util.isOnline
import com.holymusic.app.features.data.remote.model.AlbumDtoItem
import com.holymusic.app.features.data.remote.model.ArtistDtoItem
import com.holymusic.app.features.data.remote.model.CategoryDtoItem
import com.holymusic.app.features.data.remote.model.TrackBillboardDtoItem
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.data.remote.model.toTrackDtoItem
import com.holymusic.app.features.presentation.Screens
import com.holymusic.app.features.presentation.home.components.Artists
import com.holymusic.app.features.presentation.home.components.AudioAlbum
import com.holymusic.app.features.presentation.home.components.AudioTrack
import com.holymusic.app.features.presentation.home.components.Audios
import com.holymusic.app.features.presentation.home.components.ExitDialogue
import com.holymusic.app.features.presentation.home.components.ForwardIcon
import com.holymusic.app.features.presentation.home.components.ShortPlayDialogue
import com.holymusic.app.features.presentation.home.components.Trending
import com.holymusic.app.features.presentation.home.components.VideoAlbum
import com.holymusic.app.features.presentation.home.components.VideoTrack
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController,
    navToAudioPlayer: (CategoryDtoItem, TracksDtoItem) -> Unit,
    navToVideoPlayer: (CategoryDtoItem, TracksDtoItem) -> Unit,
    navToArtist: (CategoryDtoItem) -> Unit,
    navToArtistItem: (CategoryDtoItem, ArtistDtoItem, String) -> Unit,
    navToAudio: (CategoryDtoItem) -> Unit,
    navToVideo: (CategoryDtoItem) -> Unit,
    navToAlbum: (CategoryDtoItem) -> Unit,
    navToAlbumItem: (CategoryDtoItem, AlbumDtoItem) -> Unit,
    navToVideoAlbum: (CategoryDtoItem) -> Unit,
    navToVideoAlbumItem: (CategoryDtoItem, AlbumDtoItem) -> Unit,
    navToProfile: () -> Unit,
    navToChoosePlan: () -> Unit,
    navToLogin: () -> Unit,
    closeLoader: () -> Unit,
    navToMyFavorites: () -> Unit,
    navToVideoScholar: (CategoryDtoItem) -> Unit,
    navToScholarVideoPlayer: (CategoryDtoItem, TracksDtoItem) -> Unit,
    navToKhatameQuranVideoPlayer: (CategoryDtoItem, TracksDtoItem) -> Unit,
    navToDownloads: (CategoryDtoItem) -> Unit,
    navToPlans: () -> Unit,
    scope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    navToUserJourney: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val width = LocalConfiguration.current.screenWidthDp
    val audioImageWidth = (width / 2.5).dp
    val videoImageWidth = (width / 1.5).dp
    val videoImageHeight = (width / 1.5 / 16 * 9).dp
    val bannerHeight = (width * 9 / 16).dp
    val activity = LocalContext.current as Activity
    val context = LocalContext.current
    val systemUiController: SystemUiController = rememberSystemUiController()
    val orientation = LocalConfiguration.current.orientation

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true
        )
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Drawer(
                    mobile = state.mobile,
                    closeDrawer = {
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    navToArtist = navToArtist,
                    navToAudio = navToAudio,
                    navToVideo = navToVideo,
                    navToAlbum = navToAlbum,
                    navToVideoAlbum = navToVideoAlbum,
                    navToMyFavorites = navToMyFavorites,
                    navToDownloads = navToDownloads,
                    navToLogin = navToLogin,
                    navToChoosePlan = navToChoosePlan,
                    plan = state.subStatusDtoItem.servicename ?: "",
                    navToMyPlan = navToPlans
                )
            }
        }
    ) {
        BottomSheetScaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                AppBar(
                    title = stringResource(id = R.string.app_name),
                    titleColor = MaterialTheme.colorScheme.primary,
                    navController = navController,
                    icon = R.drawable.logo_small,
                    actions = listOf(
//                        ActionItem(image = R.drawable.user_journey, action = {
//                            navToUserJourney()
//                        }),
                        ActionItem(Icons.Filled.Person, action = {
                            navToProfile()
                        })
                    ),
                    isBack = false,
                    openDrawer = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    },
                    suffix = {
                        Box(modifier = Modifier.height(16.dp)) {
                            Switch(checked = state.isDark == 2, onCheckedChange = {
                                viewModel.isDark(it)
                            }, modifier = Modifier.scale(.75f))
                        }
                    }
                )
            },
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
                        videoType = state.videoType,
                        backHandler = false
                    )
            },
            scaffoldState = scaffoldState,
            sheetPeekHeight = if (!state.currentTrack.streamUrl.isNullOrEmpty()) 60.dp else 0.dp,
            sheetShape = RectangleShape,
        ) { paddingValues ->

            if (!state.isLoaderClosed && !state.isLoading) {
                viewModel.loaderClosed()
                closeLoader()
            }

            val isExit = remember {
                mutableStateOf(false)
            }

            BackHandler {
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    systemUiController.isStatusBarVisible = true
                } else if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                    scope.launch {
                        scaffoldState.bottomSheetState.partialExpand()
                    }
                } else {
                    isExit.value = true
                }
            }

            if (isExit.value)
                ExitDialogue(activity) {
                    isExit.value = false
                }

            val pagerState = rememberPagerState(initialPage = 0)


            if (!state.shortPlay.targetId.isNullOrEmpty() || !state.promotions.data.isNullOrEmpty()) {
                ShortPlayDialogue(
                    onDismissBillboard = {
                        viewModel.closeShortPlay()
                        viewModel.closePromotions()
                    },
                    onDismissPromotions = viewModel::dismissPromotions,
                    billBoard = state.shortPlay,
                    play = {
                        viewModel.closeShortPlay()

                        when (it.contentType) {
                            AppConstants.typeAudio -> {
                                navToAudioPlayer(
                                    CategoryDtoItem(
                                        name = AppConstants.audioTraks,
                                        icon = R.drawable.audio
                                    ), it.toTrackDtoItem()
                                )
                            }

                            AppConstants.typeVideo -> {
                                scope.launch {
                                    viewModel.playVideo(
                                        it.toTrackDtoItem(),
                                        Enums.VideoType.Artist.name
                                    )
                                    delay(100)
                                    scaffoldState.bottomSheetState.expand()
                                }
                            }
                        }
                    },
                    promotions = state.promotions.data ?: emptyList(),
                    navToLogin = navToLogin,
                    navToChoosePlan = navToChoosePlan
                )
            }

            LaunchedEffect(Unit) {
                viewModel.getMobile()
                viewModel.checkSubscriptionStatus()

                if (state.isLoading) {
                    viewModel.init()
                }
                while (true) {
                    delay(5000)
                    with(pagerState) {
                        val target = if (currentPage < pageCount - 1) currentPage + 1 else 0
                        tween<Float>(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        )
                        animateScrollToPage(page = target)
                    }
                }

            }

            DisposableEffect(Unit) {
                onDispose {
                    viewModel.closeService()
                }
            }


            if (!isOnline(context))
                Box(modifier = Modifier.padding(paddingValues)) {
                    NoInternet {
                        if (isOnline(context)) {
                            navController.navigate(Screens.HOME_SCREEN) {
                                popUpTo(0)
                            }
                        }
                    }
                }
            else if (state.isLoading)
                Loader(paddingValues)
            else
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.background)
                ) {
                    LazyColumn(
                        modifier = modifier
                            .weight(1f)
                            .testTag("home"),
                        content = {

                            item {
                                if (!state.trackBillboard.data.isNullOrEmpty())
                                    HorizontalPager(
                                        count = remember {
                                            if (state.trackBillboard.data != null) state.trackBillboard.data?.size
                                                ?: 0 else 0
                                        }, state = pagerState
                                    ) {
                                        val item = remember {
                                            state.trackBillboard.data?.get(it)
                                                ?: TrackBillboardDtoItem()
                                        }
                                        AsyncImage(
                                            model = item.contentBaseUrl + item.imageUrl,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .padding(10.dp)
                                                .width(width.dp)
                                                .height(width.dp)
                                                .clip(shape = RoundedCornerShape(15.dp))
                                                .clickable {
                                                    if (item.contentType == AppConstants.typeAudio) {
                                                        navToAudioPlayer(
                                                            CategoryDtoItem(
                                                                name = AppConstants.audioTraks,
                                                                icon = R.drawable.audio
                                                            ),
                                                            item.toTrackDtoItem()
                                                        )
                                                    } else {
                                                        navToVideoPlayer(
                                                            CategoryDtoItem(
                                                                name = AppConstants.videoTracks,
                                                                icon = R.drawable.video
                                                            ),
                                                            item.toTrackDtoItem()
                                                        )
                                                    }
                                                }
                                        )
                                    }
                            }
                            item {
                                if (!state.trackBillboard.data.isNullOrEmpty())

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier
                                            .padding(top = 8.dp, bottom = 16.dp)
                                            .fillMaxWidth(),
                                    ) {
                                        for (i in 0 until pagerState.pageCount) {
                                            val isSelected = i == pagerState.currentPage
                                            Box(modifier = Modifier.padding(horizontal = 5.dp)) {
                                                PageIndicatorView(
                                                    isSelected = isSelected,
                                                    selectedColor = MaterialTheme.colorScheme.primary,
                                                    defaultColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                                    defaultRadius = 8.dp,
                                                    selectedLength = 8.dp,
                                                    animationDurationInMillis = 300,
                                                )
                                            }
                                        }
                                    }
                            }

                            if (!state.latestReleaseTrack.data.isNullOrEmpty())
                                item {
                                    Audios(
                                        category = CategoryDtoItem(
                                            state.latestReleaseAlbum.title ?: "",
                                            icon = R.drawable.audio
                                        ),
                                        album = state.latestReleaseAlbum,
                                        tracks = state.latestReleaseTrack,
                                        navToAlbumItem = { cat, item ->
                                            navToAlbumItem(cat, item)
                                        },
                                        navToAudioPlayer = navToAudioPlayer,
                                        width = audioImageWidth
                                    )
                                }

                            item {
                                if (!state.artist.data.isNullOrEmpty())
                                    ForwardIcon(
                                        category = CategoryDtoItem(
                                            name = AppConstants.gajalArtist,
                                            icon = R.drawable.artist
                                        ), navToCategory = navToArtist
                                    )
                                Artists(state.artist, navToContent = { artist ->
                                    navToArtistItem(
                                        CategoryDtoItem(
                                            name = AppConstants.gajalArtist,
                                            icon = R.drawable.artist
                                        ), artist,
                                        AppConstants.typeAudio
                                    )
                                })
                            }

                            if (state.trending.isNotEmpty())
                                item {
                                    Trending(track = state.trending[0], onTap = {
                                        if (it.contentCategory == AppConstants.typeAudio) {
                                            navToAudioPlayer(
                                                CategoryDtoItem(
                                                    name = AppConstants.trending,
                                                    icon = R.drawable.audio
                                                ), it.toTrackDtoItem()
                                            )
                                        } else if (it.contentCategory == AppConstants.typeVideo) {
                                            navToVideoPlayer(
                                                CategoryDtoItem(
                                                    name = AppConstants.trending,
                                                    icon = R.drawable.video
                                                ), it.toTrackDtoItem()
                                            )
                                        }
                                    })
                                }

                            if (!state.jonoprioTracks.data.isNullOrEmpty())
                                item {
                                    Audios(
                                        category = CategoryDtoItem(
                                            state.jonoprioAlbum.title ?: "",
                                            icon = R.drawable.audio
                                        ),
                                        album = state.jonoprioAlbum,
                                        tracks = state.jonoprioTracks,
                                        navToAlbumItem = { cat, item ->
                                            navToAlbumItem(cat, item)
                                        },
                                        navToAudioPlayer = navToAudioPlayer,
                                        width = audioImageWidth
                                    )
                                }

                            if (!state.shishuTracks.data.isNullOrEmpty())
                                item {
                                    Audios(
                                        category = CategoryDtoItem(
                                            state.shishuAlbum.title ?: "",
                                            icon = R.drawable.audio
                                        ),
                                        album = state.shishuAlbum,
                                        tracks = state.shishuTracks,
                                        navToAlbumItem = { cat, item ->
                                            navToAlbumItem(cat, item)
                                        },
                                        navToAudioPlayer = navToAudioPlayer,
                                        width = audioImageWidth
                                    )
                                }

                            if (!state.deshTracks.data.isNullOrEmpty())
                                item {
                                    Audios(
                                        category = CategoryDtoItem(
                                            state.deshAlbum.title ?: "",
                                            icon = R.drawable.audio
                                        ),
                                        album = state.deshAlbum,
                                        tracks = state.deshTracks,
                                        navToAlbumItem = { cat, item ->
                                            navToAlbumItem(cat, item)
                                        },
                                        navToAudioPlayer = navToAudioPlayer,
                                        width = audioImageWidth
                                    )
                                }

                            if (!state.maTracks.data.isNullOrEmpty())
                                item {
                                    Audios(
                                        category = CategoryDtoItem(
                                            state.maAlbum.title ?: "",
                                            icon = R.drawable.audio
                                        ),
                                        album = state.maAlbum,
                                        tracks = state.maTracks,
                                        navToAlbumItem = { cat, item ->
                                            navToAlbumItem(cat, item)
                                        },
                                        navToAudioPlayer = navToAudioPlayer,
                                        width = audioImageWidth
                                    )
                                }

                            if (!state.babarTracks.data.isNullOrEmpty())
                                item {
                                    Audios(
                                        category = CategoryDtoItem(
                                            state.babarAlbum.title ?: "",
                                            icon = R.drawable.audio
                                        ),
                                        album = state.babarAlbum,
                                        tracks = state.babarTracks,
                                        navToAlbumItem = { cat, item ->
                                            navToAlbumItem(cat, item)
                                        },
                                        navToAudioPlayer = navToAudioPlayer,
                                        width = audioImageWidth
                                    )
                                }

                            if (!state.selectedTracks.data.isNullOrEmpty())
                                item {
                                    Audios(
                                        category = CategoryDtoItem(
                                            state.selectedAlbum.title ?: "",
                                            icon = R.drawable.audio
                                        ),
                                        album = state.selectedAlbum,
                                        tracks = state.selectedTracks,
                                        navToAlbumItem = { cat, item ->
                                            navToAlbumItem(cat, item)
                                        },
                                        navToAudioPlayer = navToAudioPlayer,
                                        width = audioImageWidth
                                    )
                                }

                            if (state.trending.size > 1)
                                item {
                                    Trending(track = state.trending[1], onTap = {
                                        if (it.contentCategory == AppConstants.typeAudio) {
                                            navToAudioPlayer(
                                                CategoryDtoItem(
                                                    name = AppConstants.trending,
                                                    icon = R.drawable.audio
                                                ), it.toTrackDtoItem()
                                            )
                                        } else if (it.contentCategory == AppConstants.typeVideo) {
                                            navToVideoPlayer(
                                                CategoryDtoItem(
                                                    name = AppConstants.trending,
                                                    icon = R.drawable.video
                                                ), it.toTrackDtoItem()
                                            )
                                        }
                                    })
                                }

                            if (!state.syedTracks.data.isNullOrEmpty())
                                item {
                                    Audios(
                                        category = CategoryDtoItem(
                                            state.syedAlbum.title ?: "",
                                            icon = R.drawable.audio
                                        ),
                                        album = state.syedAlbum,
                                        tracks = state.syedTracks,
                                        navToAlbumItem = { cat, item ->
                                            navToAlbumItem(cat, item)
                                        },
                                        navToAudioPlayer = navToAudioPlayer,
                                        width = audioImageWidth
                                    )
                                }

                            if (!state.zamanTracks.data.isNullOrEmpty())
                                item {
                                    Audios(
                                        category = CategoryDtoItem(
                                            state.zamanAlbum.title ?: "",
                                            icon = R.drawable.audio
                                        ),
                                        album = state.zamanAlbum,
                                        tracks = state.zamanTracks,
                                        navToAlbumItem = { cat, item ->
                                            navToAlbumItem(cat, item)
                                        },
                                        navToAudioPlayer = navToAudioPlayer,
                                        width = audioImageWidth
                                    )
                                }

                            if (!state.rayhanTracks.data.isNullOrEmpty())
                                item {
                                    Audios(
                                        category = CategoryDtoItem(
                                            state.rayhanAlbum.title ?: "",
                                            icon = R.drawable.audio
                                        ),
                                        album = state.rayhanAlbum,
                                        tracks = state.rayhanTracks,
                                        navToAlbumItem = { cat, item ->
                                            navToAlbumItem(cat, item)
                                        },
                                        navToAudioPlayer = navToAudioPlayer,
                                        width = audioImageWidth
                                    )
                                }

                            item {
                                if (!state.popularTracks.data.isNullOrEmpty() && (state.popularTracks.data?.size
                                        ?: 0) > 2
                                )
                                    ForwardIcon(
                                        category = CategoryDtoItem(
                                            name = AppConstants.audioTraks,
                                            icon = R.drawable.audio,
                                            isPopular = true
                                        ), navToCategory = navToAudio
                                    )
                            }
                            items(
                                count = state.popularTracks.data?.size?.div(3) ?: 0
                            ) { column ->
                                LazyRow(contentPadding = PaddingValues(horizontal = 5.dp),
                                    content = {
                                        items(
                                            count = 3.coerceAtMost(
                                                state.popularTracks.data?.size ?: 0
                                            ),
                                            itemContent = {
                                                AudioTrack(
                                                    content = state.popularTracks.data?.get(column * 3 + it)
                                                        ?: TracksDtoItem(),
                                                    navToContent = { track ->
                                                        navToAudioPlayer(
                                                            CategoryDtoItem(
                                                                AppConstants.audioTraks,
                                                                R.drawable.audio
                                                            ), track
                                                        )
                                                    },
                                                    audioImageWidth
                                                )
                                            })
                                    })
                            }

                            item {
                                if (!state.favoriteTracks.data.isNullOrEmpty() && (state.favoriteTracks.data?.size
                                        ?: 0) > 2
                                )
                                    ForwardIcon(
                                        category = CategoryDtoItem(
                                            name = AppConstants.favorite_gajal,
                                            icon = R.drawable.audio,
                                            isFavorite = true
                                        ), navToCategory = navToAudio
                                    )
                            }
                            items(
                                count = state.favoriteTracks.data?.size?.div(3) ?: 0
                            ) { column ->
                                LazyRow(contentPadding = PaddingValues(horizontal = 5.dp),
                                    content = {
                                        items(
                                            count = 3.coerceAtMost(
                                                state.favoriteTracks.data?.size ?: 0
                                            ),
                                            itemContent = {
                                                AudioTrack(
                                                    content = state.favoriteTracks.data?.get(column * 3 + it)
                                                        ?: TracksDtoItem(),
                                                    navToContent = { track ->
                                                        navToAudioPlayer(
                                                            CategoryDtoItem(
                                                                AppConstants.favorite_gajal,
                                                                R.drawable.audio
                                                            ), track
                                                        )
                                                    },
                                                    audioImageWidth
                                                )
                                            })
                                    })
                            }

                            item {
                                if (!state.albums.data.isNullOrEmpty())
                                    ForwardIcon(
                                        category = CategoryDtoItem(
                                            name = AppConstants.audioAlbum,
                                            icon = R.drawable.audio_album
                                        ), navToCategory = navToAlbum
                                    )
                            }
                            item {
                                LazyRow(contentPadding = PaddingValues(horizontal = 5.dp),
                                    content = {
                                        items(
                                            count = state.albums.data?.size ?: 0,
                                            itemContent = {
                                                AudioAlbum(
                                                    content = state.albums.data?.get(it)
                                                        ?: AlbumDtoItem(),
                                                    navToContent = { album ->
                                                        navToAlbumItem(
                                                            CategoryDtoItem(
                                                                name = AppConstants.audioAlbum,
                                                                icon = R.drawable.audio_album
                                                            ), album
                                                        )
                                                    },
                                                    imageWidth = audioImageWidth
                                                )
                                            })
                                    })
                            }

                            if (state.trending.size > 2)
                                item {
                                    Trending(track = state.trending[2], onTap = {
                                        if (it.contentCategory == AppConstants.typeAudio) {
                                            navToAudioPlayer(
                                                CategoryDtoItem(
                                                    name = AppConstants.trending,
                                                    icon = R.drawable.audio
                                                ), it.toTrackDtoItem()
                                            )
                                        } else if (it.contentCategory == AppConstants.typeVideo) {
                                            navToVideoPlayer(
                                                CategoryDtoItem(
                                                    name = AppConstants.trending,
                                                    icon = R.drawable.video
                                                ), it.toTrackDtoItem()
                                            )
                                        }
                                    })
                                }

                            if (!state.tracks.data.isNullOrEmpty())
                                item {
                                    ForwardIcon(
                                        category = CategoryDtoItem(
                                            name = AppConstants.gajalAudio,
                                            icon = R.drawable.audio
                                        ), navToCategory = navToAudio
                                    )
                                }
                            item {
                                LazyRow(contentPadding = PaddingValues(horizontal = 5.dp),
                                    content = {
                                        items(
                                            count =
                                            state.tracks.data?.size ?: 0,
                                            itemContent = {
                                                AudioTrack(
                                                    content = state.tracks.data?.get(it)
                                                        ?: TracksDtoItem(),
                                                    navToContent = { track ->
                                                        navToAudioPlayer(
                                                            CategoryDtoItem(
                                                                name = AppConstants.gajalAudio,
                                                                icon = R.drawable.audio
                                                            ), track
                                                        )
                                                    },
                                                    audioImageWidth
                                                )
                                            })
                                    })
                            }

                            item {
                                if (!state.latestReleaseVideoTrack.data.isNullOrEmpty())
                                    ForwardIcon(
                                        category = CategoryDtoItem(
                                            name = state.latestReleaseVideoAlbum.title ?: "",
                                            icon = R.drawable.video
                                        ), navToCategory = navToVideo
                                    )
                            }
                            item {
                                LazyRow(contentPadding = PaddingValues(horizontal = 5.dp),
                                    content = {
                                        items(
                                            count =
                                            state.latestReleaseVideoTrack.data?.size ?: 0,
                                            itemContent = {
                                                VideoTrack(
                                                    content = state.latestReleaseVideoTrack.data?.get(
                                                        it
                                                    )
                                                        ?: TracksDtoItem(),
                                                    navToContent = { track ->
                                                        scope.launch {
                                                            viewModel.playVideo(
                                                                track,
                                                                Enums.VideoType.Artist.name
                                                            )
                                                            delay(100)
                                                            scaffoldState.bottomSheetState.expand()
                                                        }
                                                    },
                                                    imageWidth = videoImageWidth,
                                                    imageHeight = videoImageHeight
                                                )
                                            })
                                    })
                            }

                            item {
                                if (!state.videoTracks.data.isNullOrEmpty())
                                    ForwardIcon(
                                        category = CategoryDtoItem(
                                            name = AppConstants.gajalVideo,
                                            icon = R.drawable.video
                                        ), navToCategory = navToVideo
                                    )
                            }
                            item {
                                LazyRow(contentPadding = PaddingValues(horizontal = 5.dp),
                                    content = {
                                        items(
                                            count =
                                            state.videoTracks.data?.size ?: 0,
                                            itemContent = {
                                                VideoTrack(
                                                    content = state.videoTracks.data?.get(it)
                                                        ?: TracksDtoItem(),
                                                    navToContent = { track ->
                                                        scope.launch {
                                                            viewModel.playVideo(
                                                                track,
                                                                Enums.VideoType.Artist.name
                                                            )
                                                            delay(100)
                                                            scaffoldState.bottomSheetState.expand()
                                                        }
                                                    },
                                                    imageWidth = videoImageWidth,
                                                    imageHeight = videoImageHeight
                                                )
                                            })
                                    })
                            }

                            item {
                                if (!state.popularVideoAlbumTrack.data.isNullOrEmpty())
                                    ForwardIcon(
                                        category = CategoryDtoItem(
                                            name = state.popularVideoAlbum.title ?: "",
                                            icon = R.drawable.video
                                        ), navToCategory = navToVideo
                                    )
                            }
                            item {
                                LazyRow(contentPadding = PaddingValues(horizontal = 5.dp),
                                    content = {
                                        items(
                                            count =
                                            state.popularVideoAlbumTrack.data?.size ?: 0,
                                            itemContent = {
                                                VideoTrack(
                                                    content = state.popularVideoAlbumTrack.data?.get(
                                                        it
                                                    )
                                                        ?: TracksDtoItem(),
                                                    navToContent = { track ->
                                                        scope.launch {
                                                            viewModel.playVideo(
                                                                track,
                                                                Enums.VideoType.Artist.name
                                                            )
                                                            delay(100)
                                                            scaffoldState.bottomSheetState.expand()
                                                        }
                                                    },
                                                    imageWidth = videoImageWidth,
                                                    imageHeight = videoImageHeight
                                                )
                                            })
                                    })
                            }

                            item {
                                if (!state.videoAlbums.data.isNullOrEmpty())
                                    ForwardIcon(
                                        category = CategoryDtoItem(
                                            name = AppConstants.videoAlbum,
                                            icon = R.drawable.video_album
                                        ), navToCategory = navToVideoAlbum
                                    )
                            }
                            item {
                                LazyRow(contentPadding = PaddingValues(horizontal = 5.dp),
                                    content = {
                                        items(
                                            count =
                                            state.videoAlbums.data?.size ?: 0,
                                            itemContent = {
                                                VideoAlbum(
                                                    content = state.videoAlbums.data?.get(it)
                                                        ?: AlbumDtoItem(),
                                                    navToContent = { album ->
                                                        navToVideoAlbumItem(
                                                            CategoryDtoItem(
                                                                name = AppConstants.videoAlbum,
                                                                icon = R.drawable.video_album
                                                            ), album
                                                        )
                                                    },
                                                    imageWidth = videoImageWidth,
                                                    imageHeight = videoImageHeight
                                                )
                                            })
                                    })
                            }

                            item {
                                if (!state.popularVideoTracks.data.isNullOrEmpty() && (state.popularVideoTracks.data?.size
                                        ?: 0) > 1
                                )
                                    ForwardIcon(
                                        category = CategoryDtoItem(
                                            name = AppConstants.videoTracks,
                                            icon = R.drawable.video,
                                            isPopular = true
                                        ), navToCategory = navToVideo
                                    )
                            }
                            items(
                                count = state.popularVideoTracks.data?.size?.div(2) ?: 0
                            ) { column ->
                                LazyRow(contentPadding = PaddingValues(horizontal = 5.dp),
                                    content = {
                                        items(
                                            count = 2.coerceAtMost(
                                                state.popularVideoTracks.data?.size ?: 0
                                            ),
                                            itemContent = {
                                                VideoTrack(
                                                    content = state.popularVideoTracks.data?.get(
                                                        column * 2 + it
                                                    )
                                                        ?: TracksDtoItem(),
                                                    navToContent = { track ->
                                                        scope.launch {
                                                            viewModel.playVideo(
                                                                track,
                                                                Enums.VideoType.Artist.name
                                                            )
                                                            delay(100)
                                                            scaffoldState.bottomSheetState.expand()
                                                        }
                                                    },
                                                    imageWidth = videoImageWidth,
                                                    imageHeight = videoImageHeight
                                                )
                                            })
                                    })

                            }

                            item {
                                if (!state.favoriteVideoTracks.data.isNullOrEmpty() && (state.favoriteVideoTracks.data?.size
                                        ?: 0) > 1
                                )
                                    ForwardIcon(
                                        category = CategoryDtoItem(
                                            name = AppConstants.favorite_gajalVideo,
                                            icon = R.drawable.video,
                                            isFavorite = true
                                        ), navToCategory = navToVideo
                                    )
                            }
                            items(
                                count = state.favoriteVideoTracks.data?.size?.div(2) ?: 0
                            ) { column ->
                                LazyRow(contentPadding = PaddingValues(horizontal = 5.dp),
                                    content = {
                                        items(
                                            count = 2.coerceAtMost(
                                                state.favoriteVideoTracks.data?.size
                                                    ?: 0
                                            ),
                                            itemContent = {
                                                VideoTrack(
                                                    content = state.favoriteVideoTracks.data?.get(
                                                        column * 2 + it
                                                    )
                                                        ?: TracksDtoItem(),
                                                    navToContent = { track ->
                                                        scope.launch {
                                                            viewModel.playVideo(
                                                                track,
                                                                Enums.VideoType.Artist.name
                                                            )
                                                            delay(100)
                                                            scaffoldState.bottomSheetState.expand()
                                                        }
                                                    },
                                                    imageWidth = videoImageWidth,
                                                    imageHeight = videoImageHeight
                                                )
                                            })
                                    })
                            }

                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        })

                    BottomPlayerView(
                        viewModel.audioExoPlayer,
                        navToChoosePlan = navToChoosePlan,
                        navToLogin = navToLogin
                    )
                }
        }
    }
}
