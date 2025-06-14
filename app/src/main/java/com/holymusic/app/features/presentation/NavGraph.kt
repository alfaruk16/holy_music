package com.holymusic.app.features.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.holymusic.app.R
import com.holymusic.app.core.util.AppConstants
import com.holymusic.app.features.data.remote.model.AlbumDtoItem
import com.holymusic.app.features.data.remote.model.ArtistDtoItem
import com.holymusic.app.features.data.remote.model.CategoryDtoItem
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.presentation.album_audio_player.AlbumAudioPlayerScreen
import com.holymusic.app.features.presentation.album_audios.AlbumAudiosScreen
import com.holymusic.app.features.presentation.album_video_player.AlbumVideoPlayerScreen
import com.holymusic.app.features.presentation.album_videos.AlbumVideosScreen
import com.holymusic.app.features.presentation.artist_audio_player.ArtistAudioPlayerScreen
import com.holymusic.app.features.presentation.artist_audios.ArtistAudiosScreen
import com.holymusic.app.features.presentation.artist_video_player.ArtistVideoPlayerScreen
import com.holymusic.app.features.presentation.artist_videos.ArtistVideosScreen
import com.holymusic.app.features.presentation.artists.ArtistsScreen
import com.holymusic.app.features.presentation.audio_album.AudioAlbumScreen
import com.holymusic.app.features.presentation.audio_artists.AudioArtistScreen
import com.holymusic.app.features.presentation.choose_plan.ChoosePlanScreen
import com.holymusic.app.features.presentation.downloads.DownloadsScreen
import com.holymusic.app.features.presentation.forgot_password.ForgotPasswordScreen
import com.holymusic.app.features.presentation.login.LoginScreen
import com.holymusic.app.features.presentation.main.MainScreen
import com.holymusic.app.features.presentation.my_favorites.MyFavoritesScreen
import com.holymusic.app.features.presentation.my_plans.MyPlanScreen
import com.holymusic.app.features.presentation.no_internet.NoInternetScreen
import com.holymusic.app.features.presentation.otp.OtpScreen
import com.holymusic.app.features.presentation.profile.ProfileScreen
import com.holymusic.app.features.presentation.reset_password.ResetPasswordScreen
import com.holymusic.app.features.presentation.search.SearchScreen
import com.holymusic.app.features.presentation.sign_up.SignUpScreen
import com.holymusic.app.features.presentation.ssl.SSLScreen
import com.holymusic.app.features.presentation.update_profile.UpdateProfileScreen
import com.holymusic.app.features.presentation.user_journey.UserJourneyVideoPlayerScreen
import com.holymusic.app.features.presentation.video_album.VideoAlbumScreen
import com.holymusic.app.features.presentation.video_artists.VideoArtistScreen
import com.holymusic.app.features.presentation.welcome_screen.WelcomeScreen
import com.google.gson.Gson

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    //coroutineScope: CoroutineScope = rememberCoroutineScope(),
    startDestination: String,
    navAction: NavigationActions = remember(navController) {
        NavigationActions(navController)
    },
    deepLink: String,
    deepLinkId: String
) {
    //val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    //val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable(
            ScreenRoute.UserJourneyVideoPlayer.route, arguments = listOf(
                navArgument(ScreenArgs.TITLE) { type = NavType.StringType },
                navArgument(ScreenArgs.CATEGORY) { type = NavType.StringType; nullable = true }
            )
        ) { entry ->
            val category = Gson().fromJson(
                entry.arguments?.getString(ScreenArgs.CATEGORY)!!,
                CategoryDtoItem::class.java
            )
            UserJourneyVideoPlayerScreen(
                navController = navController
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
                    navAction.navToArtistVideoPlayer(
                        category, track
                    )
                },
                navToChoosePlan = {},
                navToLogin = { }
            )
        }

        composable(
            ScreenRoute.Welcome.route
        ) {
            WelcomeScreen(navToLogin = { navAction.navToLogin() })
        }

        composable(ScreenRoute.NoInternet.route) {
            NoInternetScreen(navToHome = { navAction.navToMain() })
        }

        composable(
            ScreenRoute.Login.route,
            arguments = listOf(navArgument(ScreenArgs.TITLE) {
                type = NavType.StringType; nullable = true
            })
        ) {
            LoginScreen(navController = navController,
                navToSignUp = { navAction.navToSignUp() },
                navToChoosePlan = { navAction.navToChoosePlan() },
                navToForgotPassword = { navAction.navToForgotPassword() })
        }

        composable(
            ScreenRoute.SignUp.route
        ) {
            SignUpScreen(
                navController = navController,
                navToChoosePlan = { navAction.navToChoosePlan() })
        }

        composable(
            ScreenRoute.UpdateProfile.route,
            arguments = listOf(navArgument(ScreenArgs.TITLE) {
                type = NavType.StringType
            }, navArgument(ScreenArgs.CONTENT) { type = NavType.StringType })
        ) {
            UpdateProfileScreen(
                navController = navController
            )
        }
        composable(
            ScreenRoute.ForgetPassword.route
        ) {
            ForgotPasswordScreen(
                navController = navController,
                navToOtp = { mobile ->
                    navAction.navToOtp(mobile)
                })
        }

        composable(
            ScreenRoute.ResetPassword.route
        ) {
            ResetPasswordScreen(
                navController = navController,
                navToLogin = { mobile -> navAction.navToLoginForLogOut(mobile) }
            )
        }

        composable(ScreenRoute.Otp.route, arguments = listOf(navArgument(ScreenArgs.TITLE) {
            type = NavType.StringType
        })) {
            OtpScreen(
                navController = navController,
                navToResetPassword = { mobile -> navAction.navToResetPassword(mobile) })
        }

        composable(ScreenRoute.Main.route) {
            val mainNavController: NavHostController = rememberNavController()

            MainScreen(
                navController = mainNavController,

                navToAudioPlayer = { category, track ->
                    navAction.navToAudioPlayer(category = category, track = track)
                },

                navToVideoPlayer = { category, track ->
                    navAction.navToArtistVideoPlayer(
                        category = category,
                        track = track
                    )
                },
                navToProfile = { navAction.navToProfile() },
                navToArtist = { category -> navAction.navToArtists(category = category) },
                navToAudio = { category -> navAction.navToAudio(category) },
                navToVideo = { category -> navAction.navToVideo(category) },
                navToAlbum = { category ->
                    navAction.navToAudioAlbum(category = category)
                },
                navToVideoAlbum = { category ->
                    navAction.navToVideoAlbum(category = category)
                },
                navToArtistItem = { category, artist, type ->
                    navAction.navToArtistAudio(category, artist)
                }, navToVideoArtistItem = { category, artist ->
                    navAction.navToArtistVideo(category, artist)
                },
                navToAlbumItem = { category, album ->
                    navAction.navToAlbumAudio(category, album)
                },
                navToVideoAlbumItem = { category, album ->
                    navAction.navToAlbumVideo(category, album)
                },
                navToChoosePlan = { navAction.navToChoosePlan() },
                navToLogin = { navAction.navToLogin() },
                navToFavorite = { navAction.navToMyFavorites() },
                navToKhatameQuran = {  },
                navToVideoScholar = { },
                navToScholarVideoPlayer = {cat, track ->},
                navToKhatameQuranVideoPlayer = { cat, track -> },
                navToDownloads = {
                    navAction.navToDownloads()
                },
                navToPlans = {
                    navAction.navToMyPlan()
                },
                navToUserJourney = {
                    navAction.navToUserJourneyVideoPlayer(
                        CategoryDtoItem(
                            icon = R.drawable.user_journey,
                            name = "User Journey"
                        )
                    )
                })
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
                    navAction.navToArtistAudio(artistAudioCategory, artist)
                },
                navToTrack = {
                    navAction.navToAudioPlayer(
                        audioCategory, it
                    )
                },
                navToChoosePlan = { navAction.navToChoosePlan() },
                navToLogin = { navAction.navToLogin() },
                navToVideoPlayer = {
                    navAction.navToArtistVideoPlayer(
                        videoCategory, it
                    )
                },
                navController = navController
            )
        }
        composable(ScreenRoute.ChoosePlan.route) {
            ChoosePlanScreen(navController = navController,
                navToSSL = { url -> navAction.navToSSL(url) },
                navToLogin = { navAction.navToLogin() })
        }

        composable(ScreenRoute.MyPlan.route) {
            MyPlanScreen(navController = navController)
        }

        composable(
            ScreenRoute.SSL.route,
            arguments = listOf(navArgument(ScreenArgs.TITLE) { type = NavType.StringType })
        ) { SSLScreen(navController = navController) }

        composable(ScreenRoute.Profile.route) {
            ProfileScreen(
                navController = navController,
                navToUpdateProfile = { profile -> navAction.navToUpdateProfile(profile) },
                navToMyPlan = { navAction.navToMyPlan() },
                navToDownloads = { navAction.navToDownloads() },
                navToMyFavorite = { navAction.navToMyFavorites() },
                navToChoosePlan = { navAction.navToChoosePlan() })
        }

        composable(
            ScreenRoute.Artists.route, arguments = listOf(
                navArgument(ScreenArgs.TITLE) { type = NavType.StringType },
                navArgument(ScreenArgs.CATEGORY) { type = NavType.StringType; nullable = true },
            )
        ) { entry ->
            val category = Gson().fromJson(
                entry.arguments?.getString(ScreenArgs.CATEGORY)!!,
                CategoryDtoItem::class.java
            )
            ArtistsScreen(
                navToArtist = { artist ->
                    navAction.navToArtistAudio(
                        category = category,
                        artist = artist
                    )
                },
                navController = navController,
                category = category,
                navToChoosePlan = { navAction.navToChoosePlan() },
                navToLogin = { navAction.navToLogin() }
            )
        }


        composable(
            ScreenRoute.AudioArtist.route, arguments = listOf(
                navArgument(ScreenArgs.TITLE) { type = NavType.StringType },
                navArgument(ScreenArgs.CATEGORY) { type = NavType.StringType; nullable = true },
            )
        ) { entry ->
            val category = Gson().fromJson(
                entry.arguments?.getString(ScreenArgs.CATEGORY)!!,
                CategoryDtoItem::class.java
            )
            AudioArtistScreen(navToContent = { artist ->
                navAction.navToArtistAudio(
                    category = category,
                    artist = artist
                )
            },
                navController = navController,
                category = category,
                navToTrack = { tracksDtoItem ->
                    navAction.navToAudioPlayer(category, tracksDtoItem)
                },
                navToChoosePlan = { navAction.navToChoosePlan() },
                navToLogin = { navAction.navToLogin() },
                navToSearch = { navAction.navToSearch() })
        }

        composable(
            ScreenRoute.ArtistAudios.route, arguments = listOf(
                navArgument(ScreenArgs.TITLE) { type = NavType.StringType },
                navArgument(ScreenArgs.CATEGORY) { type = NavType.StringType; nullable = true },
                navArgument(ScreenArgs.CONTENT) { type = NavType.StringType; nullable = true },
            )
        ) { entry ->
            val category = Gson().fromJson(
                entry.arguments?.getString(ScreenArgs.CATEGORY)!!,
                CategoryDtoItem::class.java
            )

            ArtistAudiosScreen(
                navToContent = { artist ->
                    navAction.navToAudioPlayer(
                        category = category,
                        track = artist
                    )
                },
                navController = navController,
                category = category,
                navToVideoPlayer = { track ->
                    navAction.navToArtistVideoPlayer(category, track)
                },
                navToChoosePlan = { navAction.navToChoosePlan() },
                navToLogin = { navAction.navToLogin() }
            )
        }

        composable(
            ScreenRoute.MyFavorites.route
        ) {
            val category = CategoryDtoItem(
                name = AppConstants.myFavorites,
                icon = R.drawable.baseline_favorite_24
            )

            MyFavoritesScreen(
                navToContent = { artist ->
                    navAction.navToAudioPlayer(
                        category = category,
                        track = artist
                    )
                },
                navController = navController,
                category = category,
                navToVideoPlayer = { track ->
                    navAction.navToArtistVideoPlayer(category, track)
                },
                navToChoosePlan = { navAction.navToChoosePlan() },
                navToLogin = { navAction.navToLogin() }
            )
        }

        composable(
            ScreenRoute.ArtistAudioPlayer.route, arguments = listOf(
                navArgument(ScreenArgs.TITLE) { type = NavType.StringType },
                navArgument(ScreenArgs.CATEGORY) { type = NavType.StringType; nullable = true },
                navArgument(ScreenArgs.CONTENT) { type = NavType.StringType; nullable = true },
            )
        ) { entry ->
            val category = Gson().fromJson(
                entry.arguments?.getString(ScreenArgs.CATEGORY)!!,
                CategoryDtoItem::class.java
            )

            ArtistAudioPlayerScreen(
//                navToContent = {},
                navController = navController,
                category = category,
                navToChoosePlan = { navAction.navToChoosePlan() },
                navToLogin = { navAction.navToLogin() }
            )
        }

        composable(
            ScreenRoute.AlbumAudios.route, arguments = listOf(
                navArgument(ScreenArgs.TITLE) { type = NavType.StringType },
                navArgument(ScreenArgs.CATEGORY) { type = NavType.StringType; nullable = true },
                navArgument(ScreenArgs.CONTENT) { type = NavType.StringType; nullable = true },
            )
        ) { entry ->
            val category = Gson().fromJson(
                entry.arguments?.getString(ScreenArgs.CATEGORY)!!,
                CategoryDtoItem::class.java
            )

            AlbumAudiosScreen(
                navToContent = { albumTrack ->
                    navAction.navToAlbumAudioPlayer(
                        category = category,
                        track = albumTrack
                    )
                },
                navController = navController,
                category = category,
                navToChoosePlan = { navAction.navToChoosePlan() },
                navToLogin = { navAction.navToLogin() }
            )
        }

        composable(
            ScreenRoute.AudioAlbum.route, arguments = listOf(
                navArgument(ScreenArgs.TITLE) { type = NavType.StringType },
                navArgument(ScreenArgs.CATEGORY) { type = NavType.StringType; nullable = true },
            )
        ) { entry ->
            val category = Gson().fromJson(
                entry.arguments?.getString(ScreenArgs.CATEGORY)!!,
                CategoryDtoItem::class.java
            )
            AudioAlbumScreen(
                navToContent = { album ->
                    navAction.navToAlbumAudio(
                        category = category,
                        album = album
                    )
                },
                navController = navController,
                category = category,
                navToAudioPlayer = { cat, track ->
                    navAction.navToAudioPlayer(cat, track)
                }, navToChoosePlan = { navAction.navToChoosePlan() },
                navToLogin = { navAction.navToLogin() })
        }

        composable(
            ScreenRoute.AlbumAudioPlayer.route, arguments = listOf(
                navArgument(ScreenArgs.TITLE) { type = NavType.StringType },
                navArgument(ScreenArgs.CATEGORY) { type = NavType.StringType; nullable = true },
                navArgument(ScreenArgs.CONTENT) { type = NavType.StringType; nullable = true },
            )
        ) { entry ->
            val category = Gson().fromJson(
                entry.arguments?.getString(ScreenArgs.CATEGORY)!!,
                CategoryDtoItem::class.java
            )

            AlbumAudioPlayerScreen(
//                navToContent = {},
                navController = navController,
                category = category,
                navToChoosePlan = { navAction.navToChoosePlan() },
                navToLogin = { navAction.navToLogin() }
            )
        }
        composable(
            ScreenRoute.VideoArtist.route, arguments = listOf(
                navArgument(ScreenArgs.TITLE) { type = NavType.StringType },
                navArgument(ScreenArgs.CATEGORY) { type = NavType.StringType; nullable = true },
                navArgument(ScreenArgs.CONTENT) { type = NavType.StringType; nullable = true },
            )
        ) { entry ->
            val category = Gson().fromJson(
                entry.arguments?.getString(ScreenArgs.CATEGORY)!!,
                CategoryDtoItem::class.java
            )
            VideoArtistScreen(navToContent = { artist ->
                navAction.navToArtistVideo(
                    category = category,
                    artist = artist
                )
            },
                navController = navController,
                category = category,
                navToTrack = { tracksDtoItem ->
                    navAction.navToArtistVideoPlayer(category, tracksDtoItem)
                },
                navToChoosePlan = { navAction.navToChoosePlan() },
                navToLogin = { navAction.navToLogin() },
                navToSearch = { navAction.navToSearch() })
        }

        composable(
            ScreenRoute.ArtistVideos.route, arguments = listOf(
                navArgument(ScreenArgs.TITLE) { type = NavType.StringType },
                navArgument(ScreenArgs.CATEGORY) { type = NavType.StringType; nullable = true },
                navArgument(ScreenArgs.CONTENT) { type = NavType.StringType; nullable = true },
            )
        ) { entry ->
            val category = Gson().fromJson(
                entry.arguments?.getString(ScreenArgs.CATEGORY)!!,
                CategoryDtoItem::class.java
            )

            ArtistVideosScreen(
                navToContent = { artist ->
                    navAction.navToArtistVideoPlayer(
                        category = category,
                        track = artist
                    )
                },
                navController = navController,
                category = category,
                navToAudioPlayer = { track ->
                    navAction.navToAudioPlayer(category, track)
                },
                navToChoosePlan = { navAction.navToChoosePlan() },
                navToLogin = { navAction.navToLogin() }
            )
        }

        composable(
            ScreenRoute.ArtistVideoPlayer.route, arguments = listOf(
                navArgument(ScreenArgs.TITLE) { type = NavType.StringType },
                navArgument(ScreenArgs.CATEGORY) { type = NavType.StringType; nullable = true },
                navArgument(ScreenArgs.CONTENT) { type = NavType.StringType; nullable = true },
            )
        ) { entry ->
            val category = Gson().fromJson(
                entry.arguments?.getString(ScreenArgs.CATEGORY)!!,
                CategoryDtoItem::class.java
            )

            ArtistVideoPlayerScreen(
                navToContent = { navAction.navToArtistVideoPlayer(category, it) },
                navController = navController,
                navToChoosePlan = { navAction.navToChoosePlan() },
                navToLogin = { navAction.navToLogin() }
            )
        }


        composable(
            ScreenRoute.VideoAlbum.route, arguments = listOf(
                navArgument(ScreenArgs.TITLE) { type = NavType.StringType },
                navArgument(ScreenArgs.CATEGORY) { type = NavType.StringType; nullable = true },
            )
        ) { entry ->
            val category = Gson().fromJson(
                entry.arguments?.getString(ScreenArgs.CATEGORY)!!,
                CategoryDtoItem::class.java
            )
            VideoAlbumScreen(
                navToContent = { album ->
                    navAction.navToAlbumVideo(
                        category = category,
                        album = album
                    )
                },
                navController = navController,
                category = category,
                navToVideoPlayer = { _, track ->
                    navAction.navToArtistVideoPlayer(category, track)
                },
                navToChoosePlan = { navAction.navToChoosePlan() },
                navToLogin = { navAction.navToLogin() })
        }

        composable(
            ScreenRoute.AlbumVideos.route, arguments = listOf(
                navArgument(ScreenArgs.TITLE) { type = NavType.StringType },
                navArgument(ScreenArgs.CATEGORY) { type = NavType.StringType; nullable = true },
            )
        ) { entry ->
            val category = Gson().fromJson(
                entry.arguments?.getString(ScreenArgs.CATEGORY)!!,
                CategoryDtoItem::class.java
            )
            AlbumVideosScreen(
                navToContent = { track ->
                    navAction.navToAlbumVideoPlayer(
                        category = category,
                        track = track
                    )
                },
                navController = navController,
                category = category,
                navToChoosePlan = { navAction.navToChoosePlan() },
                navToLogin = { navAction.navToLogin() })
        }

        composable(
            ScreenRoute.AlbumVideoPlayer.route, arguments = listOf(
                navArgument(ScreenArgs.TITLE) { type = NavType.StringType },
                navArgument(ScreenArgs.CATEGORY) { type = NavType.StringType; nullable = true },
            )
        ) { entry ->
            val category = Gson().fromJson(
                entry.arguments?.getString(ScreenArgs.CATEGORY)!!,
                CategoryDtoItem::class.java
            )
            AlbumVideoPlayerScreen(
                navController = navController,
                navToContent = { tracksDtoItem ->
                    navAction.navToAlbumVideoPlayer(category, tracksDtoItem)
                },
                navToChoosePlan = { navAction.navToChoosePlan() },
                navToLogin = { navAction.navToLogin() })
        }

    }
    if (deepLink.isNotEmpty()) {
        val audioCategory = CategoryDtoItem(
            name = AppConstants.audioTraks,
            icon = R.drawable.audio
        )
        val audioAlbumCategory = CategoryDtoItem(
            name = AppConstants.audioAlbum,
            icon = R.drawable.audio
        )
        val videoCategory = CategoryDtoItem(
            name = AppConstants.videoTracks,
            icon = R.drawable.video
        )

        when (deepLink) {

            Screens.AUDIO_ARTIST_SCREEN -> {
                navAction.navToAudio(audioCategory)
            }

            Screens.ARTIST_AUDIOS_SCREEN -> {
                if (deepLinkId.isNotEmpty())
                    navAction.navToArtistAudio(audioCategory, ArtistDtoItem(id = deepLinkId))
            }

            Screens.ARTIST_AUDIO_PLAYER_SCREEN -> {
                if (deepLinkId.isNotEmpty())
                    navAction.navToAudioPlayer(
                        category = audioCategory,
                        TracksDtoItem(id = deepLinkId)
                    )
            }

            Screens.AUDIO_ALBUM_SCREEN -> {
                navAction.navToAudioAlbum(audioCategory)
            }

            Screens.ALBUM_AUDIOS_SCREEN -> {
                if (deepLinkId.isNotEmpty())
                    navAction.navToAlbumAudio(
                        category = audioAlbumCategory,
                        AlbumDtoItem(id = deepLinkId)
                    )
            }

            Screens.ALBUM_AUDIO_PLAYER_SCREEN -> {
                if (deepLinkId.isNotEmpty())
                    navAction.navToAudioPlayer(
                        category = audioCategory,
                        TracksDtoItem(id = deepLinkId)
                    )
            }

            Screens.VIDEO_ARTIST_SCREEN -> {
                navAction.navToVideo(category = videoCategory)
            }

            Screens.ARTIST_VIDEOS_SCREEN -> {
                if (deepLinkId.isNotEmpty())
                    navAction.navToArtistVideo(
                        category = videoCategory,
                        ArtistDtoItem(id = deepLinkId)
                    )
            }

            Screens.ARTIST_VIDEO_PLAYER_SCREEN -> {
                if (deepLinkId.isNotEmpty())
                    navAction.navToArtistVideoPlayer(
                        category = videoCategory,
                        TracksDtoItem(id = deepLinkId)
                    )
            }

            Screens.VIDEO_ALBUM_SCREEN -> {
                navAction.navToVideoAlbum(category = videoCategory)
            }

            Screens.ALBUM_VIDEOS_SCREEN -> {
                if (deepLinkId.isNotEmpty())
                    navAction.navToAlbumVideo(
                        category = videoCategory,
                        AlbumDtoItem(id = deepLinkId)
                    )
            }

            Screens.ALBUM_VIDEO_PLAYER_SCREEN -> {
                if (deepLinkId.isNotEmpty())
                    navAction.navToArtistVideoPlayer(
                        category = videoCategory,
                        TracksDtoItem(id = deepLinkId)
                    )
            }

        }
    }
}