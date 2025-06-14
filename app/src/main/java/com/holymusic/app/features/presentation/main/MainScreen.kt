package com.holymusic.app.features.presentation.main

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.holymusic.app.R
import com.holymusic.app.core.components.Splash
import com.holymusic.app.core.util.AppConstants
import com.holymusic.app.features.data.remote.model.AlbumDtoItem
import com.holymusic.app.features.data.remote.model.ArtistDtoItem
import com.holymusic.app.features.data.remote.model.CategoryDtoItem
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.presentation.ScreenArgs
import com.holymusic.app.features.presentation.ScreenRoute
import com.holymusic.app.features.presentation.audio_artists.AudioArtistScreen
import com.holymusic.app.features.presentation.downloads.DownloadsScreen
import com.holymusic.app.features.presentation.home.HomeScreen
import com.holymusic.app.features.presentation.search.SearchScreen
import com.holymusic.app.features.presentation.video_artists.VideoArtistScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
    navController: NavHostController,
    navItems: List<ScreenRoute> = listOf(
        ScreenRoute.Home,
        ScreenRoute.AudioArtist,
        ScreenRoute.VideoArtist,
        ScreenRoute.Downloads,
        ScreenRoute.Search
    ),
    navToAudioPlayer: (CategoryDtoItem, TracksDtoItem) -> Unit,
    navToVideoPlayer: (CategoryDtoItem, TracksDtoItem) -> Unit,
    navToProfile: () -> Unit,
    navToArtist: (CategoryDtoItem) -> Unit,
    navToAudio: (CategoryDtoItem) -> Unit,
    navToVideo: (CategoryDtoItem) -> Unit,
    navToAlbum: (CategoryDtoItem) -> Unit,
    navToAlbumItem: (CategoryDtoItem, AlbumDtoItem) -> Unit,
    navToVideoAlbum: (CategoryDtoItem) -> Unit,
    navToVideoAlbumItem: (CategoryDtoItem, AlbumDtoItem) -> Unit,
    navToArtistItem: (CategoryDtoItem, ArtistDtoItem, String) -> Unit,
    navToVideoArtistItem: (CategoryDtoItem, ArtistDtoItem) -> Unit,
    navToChoosePlan: () -> Unit,
    navToLogin: () -> Unit,
    navToFavorite: () -> Unit,
    navToKhatameQuran: () -> Unit,
    navToVideoScholar: (CategoryDtoItem) -> Unit,
    navToScholarVideoPlayer: (CategoryDtoItem, TracksDtoItem) -> Unit,
    navToKhatameQuranVideoPlayer: (CategoryDtoItem, TracksDtoItem) -> Unit,
    navToDownloads: (CategoryDtoItem) -> Unit,
    navToPlans: () -> Unit,
    navToUserJourney: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val config = LocalConfiguration.current

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .height(if (viewModel.audioExoPlayer.isVideoPlaying.value == true && config.orientation == Configuration.ORIENTATION_LANDSCAPE) 0.dp else 64.dp)
                    .fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                content = {
                    navItems.onEach { screen ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any {
                                it.route == screen.route
                            } == true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.inverseSurface
                            ),
                            icon = {
                                if (screen.icon != null)
                                    Icon(
                                        painter = painterResource(id = screen.icon),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                            },
//                            label = {
//                                if (screen.title != R.string.app_name && screen.title != null) Text(
//                                    text = stringResource(id = screen.title),
//                            style = Typography.displaySmall
//                                )
//                            },
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }

                            },
                        )
                    }

                })

        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = ScreenRoute.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {

            composable(ScreenRoute.Home.route) {
                HomeScreen(
                    navController = navController,
                    navToAudioPlayer = navToAudioPlayer,
                    navToVideoPlayer = navToVideoPlayer,
                    navToArtist = navToArtist,
                    navToArtistItem = navToArtistItem,
                    navToAudio = navToAudio,
                    navToVideo = navToVideo,
                    navToAlbum = navToAlbum,
                    navToAlbumItem = navToAlbumItem,
                    navToVideoAlbum = navToVideoAlbum,
                    navToVideoAlbumItem = navToVideoAlbumItem,
                    navToProfile = navToProfile,
                    navToChoosePlan = navToChoosePlan,
                    navToLogin = navToLogin,
                    closeLoader = { viewModel.closeLoader() },
                    navToMyFavorites = navToFavorite,
                    navToVideoScholar = navToVideoScholar,
                    navToScholarVideoPlayer = navToScholarVideoPlayer,
                    navToKhatameQuranVideoPlayer = navToKhatameQuranVideoPlayer,
                    navToDownloads = navToDownloads,
                    navToPlans = navToPlans,
                    navToUserJourney = navToUserJourney
                )
            }

            composable(
                ScreenRoute.AudioArtist.route, arguments = listOf(
                    navArgument(ScreenArgs.TITLE) { type = NavType.StringType },
                    navArgument(ScreenArgs.CATEGORY) {
                        type = NavType.StringType; nullable = true
                    },
                )
            ) {
                val category = remember {
                    CategoryDtoItem(
                        name = AppConstants.audioTraks,
                        icon = R.drawable.audio
                    )
                }
                AudioArtistScreen(
                    navToContent = { artist ->
                        navToArtistItem(category, artist, AppConstants.typeAudio)
                    },
                    navController = navController,
                    category = category,
                    navToTrack = { tracksDtoItem ->
                        navToAudioPlayer(category, tracksDtoItem)
                    },
                    navToChoosePlan = navToChoosePlan,
                    navToLogin = navToLogin,
                    navToSearch = { navController.navigate(ScreenRoute.Search.route) }
                )
            }

            composable(
                ScreenRoute.VideoArtist.route, arguments = listOf(
                    navArgument(ScreenArgs.TITLE) { type = NavType.StringType },
                    navArgument(ScreenArgs.CATEGORY) {
                        type = NavType.StringType; nullable = true
                    },
                    navArgument(ScreenArgs.CONTENT) {
                        type = NavType.StringType; nullable = true
                    },
                )
            ) {
                val category = remember {
                    CategoryDtoItem(
                        name = AppConstants.videoTracks,
                        icon = R.drawable.video
                    )
                }
                VideoArtistScreen(
                    navToContent = { artist ->
                        navToVideoArtistItem(category, artist)
                    },
                    navController = navController,
                    category = category,
                    navToTrack = { tracksDtoItem ->
                        navToVideoPlayer(category, tracksDtoItem)
                    },
                    navToChoosePlan = navToChoosePlan,
                    navToLogin = navToLogin,
                    navToSearch = {
                        navController.navigate(ScreenRoute.Search.route)
                    }
                )
            }

            composable(
                ScreenRoute.Downloads.route
            ) {
                val category = CategoryDtoItem(
                    name = AppConstants.downloads,
                    icon = R.drawable.downloaded
                )

                DownloadsScreen(
                    navToContent = {
                    },
                    navController = navController,
                    category = category,
                    navToVideoPlayer = { track ->
                        navToVideoPlayer(
                            category, track
                        )
                    },
                    navToChoosePlan = {},
                    navToLogin = { }
                )
            }

            composable(
                ScreenRoute.Search.route
            ) {
                val artistAudioCategory = CategoryDtoItem(
                    name = AppConstants.audioTraks,
                    icon = R.drawable.audio
                )
                val artistVideoCategory = CategoryDtoItem(
                    name = AppConstants.videoTracks,
                    icon = R.drawable.video
                )
                val audioCategory = CategoryDtoItem(
                    name = AppConstants.audioTraks,
                    icon = R.drawable.audio
                )
                val videoCategory = CategoryDtoItem(
                    name = AppConstants.audioTraks,
                    icon = R.drawable.video
                )
                SearchScreen(
                    navToArtist = { artist, type ->
                        navToArtistItem(
                            if (type == AppConstants.typeAudio) artistAudioCategory else artistVideoCategory,
                            artist,
                            type
                        )
                    },
                    navToTrack = {
                        navToAudioPlayer(
                            audioCategory, it
                        )
                    },
                    navToChoosePlan = navToChoosePlan,
                    navToLogin = navToLogin,
                    navToVideoPlayer = {
                        navToVideoPlayer(
                            videoCategory, it
                        )
                    },
                    navController = navController
                )
            }
        }
    }
    Splash(state.splash)

}
