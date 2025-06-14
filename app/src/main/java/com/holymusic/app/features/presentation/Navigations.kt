package com.holymusic.app.features.presentation

import androidx.annotation.StringRes
import androidx.navigation.NavController
import com.holymusic.app.MainActivity
import com.holymusic.app.R
import com.holymusic.app.features.data.remote.model.AlbumDtoItem
import com.holymusic.app.features.data.remote.model.AlbumTrackDtoItem
import com.holymusic.app.features.data.remote.model.ArtistDtoItem
import com.holymusic.app.features.data.remote.model.CategoryDtoItem
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.data.remote.model.UserProfile
import com.holymusic.app.features.presentation.ScreenRoute.AlbumAudioPlayer.icon
import com.holymusic.app.features.presentation.ScreenRoute.AlbumAudioPlayer.title
import com.holymusic.app.features.presentation.Screens.ALBUM_AUDIOS_SCREEN
import com.holymusic.app.features.presentation.Screens.ALBUM_AUDIO_PLAYER_SCREEN
import com.holymusic.app.features.presentation.Screens.ALBUM_VIDEOS_SCREEN
import com.holymusic.app.features.presentation.Screens.ALBUM_VIDEO_PLAYER_SCREEN
import com.holymusic.app.features.presentation.Screens.ARTIST_AUDIOS_SCREEN
import com.holymusic.app.features.presentation.Screens.ARTIST_AUDIO_PLAYER_SCREEN
import com.holymusic.app.features.presentation.Screens.ARTIST_SCREEN
import com.holymusic.app.features.presentation.Screens.ARTIST_VIDEOS_SCREEN
import com.holymusic.app.features.presentation.Screens.ARTIST_VIDEO_PLAYER_SCREEN
import com.holymusic.app.features.presentation.Screens.AUDIO_ALBUM_SCREEN
import com.holymusic.app.features.presentation.Screens.AUDIO_ARTIST_SCREEN
import com.holymusic.app.features.presentation.Screens.CHOOSE_PLAN_SCREEN
import com.holymusic.app.features.presentation.Screens.DOWNLOADS_SCREEN
import com.holymusic.app.features.presentation.Screens.FORGOT_PASSWORD_SCREEN
import com.holymusic.app.features.presentation.Screens.HOME_SCREEN
import com.holymusic.app.features.presentation.Screens.LOGIN_SCREEN
import com.holymusic.app.features.presentation.Screens.MAIN_SCREEN
import com.holymusic.app.features.presentation.Screens.MY_FAVORITES_SCREEN
import com.holymusic.app.features.presentation.Screens.MY_PLAN_SCREEN
import com.holymusic.app.features.presentation.Screens.NO_INTERNET_SCREEN
import com.holymusic.app.features.presentation.Screens.OTP_SCREEN
import com.holymusic.app.features.presentation.Screens.PROFILE_SCREEN
import com.holymusic.app.features.presentation.Screens.RESET_PASSWORD_SCREEN
import com.holymusic.app.features.presentation.Screens.SEARCH_SCREEN
import com.holymusic.app.features.presentation.Screens.SIGN_UP_SCREEN
import com.holymusic.app.features.presentation.Screens.SSL_SCREEN
import com.holymusic.app.features.presentation.Screens.UPDATE_PROFILE_SCREEN
import com.holymusic.app.features.presentation.Screens.USER_JOURNEY_VIDEO_PLAYER_SCREEN
import com.holymusic.app.features.presentation.Screens.VIDEO_ALBUM_SCREEN
import com.holymusic.app.features.presentation.Screens.VIDEO_ARTIST_SCREEN
import com.holymusic.app.features.presentation.Screens.WELCOME_SCREEN
import com.google.gson.Gson

object Screens {
    const val WELCOME_SCREEN = "welcomeScreen"
    const val NO_INTERNET_SCREEN = "noInternetScreen"
    const val LOGIN_SCREEN = "loginScreen"
    const val SIGN_UP_SCREEN = "signUpScreen"
    const val UPDATE_PROFILE_SCREEN = "updateProfileScreen"
    const val FORGOT_PASSWORD_SCREEN = "forgotPasswordScreen"
    const val OTP_SCREEN = "otpScreen"
    const val RESET_PASSWORD_SCREEN = "resetPasswordScreen"
    const val PROFILE_SCREEN = "profileScreen"

    const val MAIN_SCREEN = "mainScreen"
    const val HOME_SCREEN = "homeScreen"

    const val ARTIST_SCREEN = "artistScreen"
    const val AUDIO_ARTIST_SCREEN = "audioArtistScreen"
    const val ARTIST_AUDIOS_SCREEN = "artistAudiosScreen"
    const val ARTIST_AUDIO_PLAYER_SCREEN = "artistAudioPlayerScreen"

    const val AUDIO_ALBUM_SCREEN = "audioAlbumScreen"
    const val ALBUM_AUDIOS_SCREEN = "albumAudiosScreen"
    const val ALBUM_AUDIO_PLAYER_SCREEN = "albumAudioPlayerScreen"

    const val VIDEO_ARTIST_SCREEN = "videoArtistScreen"
    const val ARTIST_VIDEOS_SCREEN = "artistVideosScreen"
    const val ARTIST_VIDEO_PLAYER_SCREEN = "artistVideoPlayerScreen"

    const val VIDEO_ALBUM_SCREEN = "videoAlbumScreen"
    const val ALBUM_VIDEOS_SCREEN = "albumVideosScreen"
    const val ALBUM_VIDEO_PLAYER_SCREEN = "albumVideoPlayerScreen"

    const val USER_JOURNEY_VIDEO_PLAYER_SCREEN = "userJourneyVideoPlayerScreen"

    const val CHOOSE_PLAN_SCREEN = "choosePlanScreen"
    const val MY_PLAN_SCREEN = "myPlanScreen"
    const val SSL_SCREEN = "sslScreen"
    const val SEARCH_SCREEN = "searchScreen"
    const val MY_FAVORITES_SCREEN = "myFavoritesScreen"
    const val DOWNLOADS_SCREEN = "downloadsScreen"
}

object ScreenArgs {
    const val TITLE = "title"
    const val CATEGORY = "category"
    const val CONTENT = "content"
}

sealed class ScreenRoute(
    val route: String,
    @StringRes val title: Int? = null,
    val icon: Int? = null,
) {

    data object Welcome : ScreenRoute(WELCOME_SCREEN, R.string.app_name, R.drawable.home)
    data object Login :
        ScreenRoute("${LOGIN_SCREEN}/{${ScreenArgs.TITLE}}", R.string.sign_in, R.drawable.home)

    data object Search : ScreenRoute(SEARCH_SCREEN, R.string.search, R.drawable.baseline_search_24)

    data object MyFavorites :
        ScreenRoute(MY_FAVORITES_SCREEN, R.string.my_favorites, R.drawable.baseline_favorite_24)

    data object Downloads : ScreenRoute(DOWNLOADS_SCREEN, R.string.downloads, R.drawable.downloaded)

    data object NoInternet : ScreenRoute(NO_INTERNET_SCREEN, R.string.app_name, R.drawable.logo)

    data object SignUp : ScreenRoute(SIGN_UP_SCREEN, R.string.create_an_account, R.drawable.home)
    data object ForgetPassword :
        ScreenRoute(FORGOT_PASSWORD_SCREEN, R.string.forgot_password, R.drawable.home)

    data object Otp :
        ScreenRoute("${OTP_SCREEN}/{${ScreenArgs.TITLE}}", R.string.verification, R.drawable.home)

    data object ResetPassword :
        ScreenRoute(
            "${RESET_PASSWORD_SCREEN}/{${ScreenArgs.TITLE}}",
            R.string.reset_password,
            R.drawable.home
        )

    data object UserJourneyVideoPlayer : ScreenRoute(
        "${USER_JOURNEY_VIDEO_PLAYER_SCREEN}/{${ScreenArgs.TITLE}}?${ScreenArgs.CATEGORY}={${ScreenArgs.CATEGORY}}",
        icon,
        title
    )

    data object Profile :
        ScreenRoute(PROFILE_SCREEN, R.string.profile, R.drawable.baseline_person_24)

    data object UpdateProfile :
        ScreenRoute(
            "${UPDATE_PROFILE_SCREEN}/{${ScreenArgs.TITLE}}?${ScreenArgs.CONTENT}={${ScreenArgs.CONTENT}}",
            R.string.edit_profile,
            R.drawable.home
        )

    data object Main : ScreenRoute(MAIN_SCREEN, R.string.app_name, R.drawable.logo_small)

    data object Home : ScreenRoute(HOME_SCREEN, R.string.app_name, R.drawable.logo_small)

    data object ChoosePlan :
        ScreenRoute(CHOOSE_PLAN_SCREEN, R.string.choose_plan, R.drawable.home)

    data object MyPlan :
        ScreenRoute(MY_PLAN_SCREEN, R.string.choose_plan, R.drawable.home)

    data object SSL :
        ScreenRoute(
            "${SSL_SCREEN}?${ScreenArgs.TITLE}={${ScreenArgs.TITLE}}",
            R.string.choose_plan,
            R.drawable.home
        )

    data object ArtistAudios : ScreenRoute(
        "${ARTIST_AUDIOS_SCREEN}/{${ScreenArgs.TITLE}}?${ScreenArgs.CATEGORY}={${ScreenArgs.CATEGORY}}&${ScreenArgs.CONTENT}={${ScreenArgs.CONTENT}}"
    )

    data object Artists : ScreenRoute(
        "${ARTIST_SCREEN}/{${ScreenArgs.TITLE}}?${ScreenArgs.CATEGORY}={${ScreenArgs.CATEGORY}}&${ScreenArgs.CONTENT}={${ScreenArgs.CONTENT}}"
    )

    data object AudioArtist : ScreenRoute(
        "${AUDIO_ARTIST_SCREEN}/{${ScreenArgs.TITLE}}?${ScreenArgs.CATEGORY}={${ScreenArgs.CATEGORY}}&${ScreenArgs.CONTENT}={${ScreenArgs.CONTENT}}",
        R.string.audio, R.drawable.audio
    )

    data object ArtistAudioPlayer : ScreenRoute(
        "${ARTIST_AUDIO_PLAYER_SCREEN}/{${ScreenArgs.TITLE}}?${ScreenArgs.CATEGORY}={${ScreenArgs.CATEGORY}}&${ScreenArgs.CONTENT}={${ScreenArgs.CONTENT}}"
    )

    data object AudioAlbum : ScreenRoute(
        "${AUDIO_ALBUM_SCREEN}/{${ScreenArgs.TITLE}}?${ScreenArgs.CATEGORY}={${ScreenArgs.CATEGORY}}&${ScreenArgs.CONTENT}={${ScreenArgs.CONTENT}}"
    )

    data object AlbumAudios : ScreenRoute(
        "${ALBUM_AUDIOS_SCREEN}/{${ScreenArgs.TITLE}}?${ScreenArgs.CATEGORY}={${ScreenArgs.CATEGORY}}&${ScreenArgs.CONTENT}={${ScreenArgs.CONTENT}}",
    )

    data object AlbumAudioPlayer : ScreenRoute(
        "${ALBUM_AUDIO_PLAYER_SCREEN}/{${ScreenArgs.TITLE}}?${ScreenArgs.CATEGORY}={${ScreenArgs.CATEGORY}}&${ScreenArgs.CONTENT}={${ScreenArgs.CONTENT}}"
    )

    data object VideoArtist : ScreenRoute(
        "${VIDEO_ARTIST_SCREEN}/{${ScreenArgs.TITLE}}?${ScreenArgs.CATEGORY}={${ScreenArgs.CATEGORY}}&${ScreenArgs.CONTENT}={${ScreenArgs.CONTENT}}",
        R.string.video, R.drawable.video
    )

    data object ArtistVideos : ScreenRoute(
        "${ARTIST_VIDEOS_SCREEN}/{${ScreenArgs.TITLE}}?${ScreenArgs.CATEGORY}={${ScreenArgs.CATEGORY}}&${ScreenArgs.CONTENT}={${ScreenArgs.CONTENT}}"
    )

    data object ArtistVideoPlayer : ScreenRoute(
        "${ARTIST_VIDEO_PLAYER_SCREEN}/{${ScreenArgs.TITLE}}?${ScreenArgs.CATEGORY}={${ScreenArgs.CATEGORY}}&${ScreenArgs.CONTENT}={${ScreenArgs.CONTENT}}"
    )


    data object VideoAlbum : ScreenRoute(
        "${VIDEO_ALBUM_SCREEN}/{${ScreenArgs.TITLE}}?${ScreenArgs.CATEGORY}={${ScreenArgs.CATEGORY}}&${ScreenArgs.CONTENT}={${ScreenArgs.CONTENT}}"
    )

    data object AlbumVideos : ScreenRoute(
        "${ALBUM_VIDEOS_SCREEN}/{${ScreenArgs.TITLE}}?${ScreenArgs.CATEGORY}={${ScreenArgs.CATEGORY}}&${ScreenArgs.CONTENT}={${ScreenArgs.CONTENT}}"
    )

    data object AlbumVideoPlayer : ScreenRoute(
        "${ALBUM_VIDEO_PLAYER_SCREEN}/{${ScreenArgs.TITLE}}?${ScreenArgs.CATEGORY}={${ScreenArgs.CATEGORY}}&${ScreenArgs.CONTENT}={${ScreenArgs.CONTENT}}"
    )

}

class NavigationActions(private val navController: NavController) {

    fun navToMyFavorites() {
        if (MainActivity.isLoggedIn) {
            navController.navigate(MY_FAVORITES_SCREEN) {
                launchSingleTop = true
                restoreState = true
            }
        } else {
            navToLogin()
        }
    }

    fun navToSearch() {
        navController.navigate(SEARCH_SCREEN) {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navToMyPlan() {
        navController.navigate(MY_PLAN_SCREEN) {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navToSSL(url: String) {
        navController.navigate("${SSL_SCREEN}?${ScreenArgs.TITLE}=${url}")
    }

    fun navToChoosePlan() {
        if (MainActivity.isLoggedIn) {
            navController.navigate(CHOOSE_PLAN_SCREEN) {
                launchSingleTop = true
                restoreState = true
            }
        } else {
            navToLogin()
        }
    }

    fun navToResetPassword(mobile: String) {
        navController.navigate("${RESET_PASSWORD_SCREEN}/$mobile") {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navToOtp(mobile: String) {
        navController.navigate("${OTP_SCREEN}/$mobile") {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navToForgotPassword() {
        navController.navigate(ScreenRoute.ForgetPassword.route) {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navToProfile() {
        if (MainActivity.isLoggedIn) {
            navController.navigate(ScreenRoute.Profile.route) {
                launchSingleTop = true
                restoreState = true
            }
        } else {
            navToLogin()
        }
    }

    fun navToMain() {
        navController.navigate(ScreenRoute.Main.route) {
            popUpTo(0)
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navToLogin() {
        navController.navigate("${LOGIN_SCREEN}/${null}") {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navToSignUp() {
        navController.navigate(ScreenRoute.SignUp.route) {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navToArtists(category: CategoryDtoItem) {
        navController.navigate(
            "${ARTIST_SCREEN}/${ScreenArgs.TITLE}?" +
                    "${ScreenArgs.CATEGORY}=${
                        Gson().toJson(category)
                    }"
        ) {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navToAudio(category: CategoryDtoItem) {
        navController.navigate(
            "${AUDIO_ARTIST_SCREEN}/${ScreenArgs.TITLE}?" +
                    "${ScreenArgs.CATEGORY}=${
                        Gson().toJson(category)
                    }"
        ) {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navToArtistAudio(category: CategoryDtoItem, artist: ArtistDtoItem) {
        navController.navigate(
            "${ARTIST_AUDIOS_SCREEN}/${ScreenArgs.TITLE}?" +
                    "${ScreenArgs.CATEGORY}=${
                        Gson().toJson(category)
                    }&" +
                    "${ScreenArgs.CONTENT}=${Gson().toJson(artist)}"
        ) {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navToAlbumAudio(category: CategoryDtoItem, album: AlbumDtoItem) {

        navController.navigate(
            "${ALBUM_AUDIOS_SCREEN}/${ScreenArgs.TITLE}?" +
                    "${ScreenArgs.CATEGORY}=${
                        Gson().toJson(category)
                    }&" +
                    "${ScreenArgs.CONTENT}=${Gson().toJson(album)}"
        ) {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navToVideo(category: CategoryDtoItem) {
        navController.navigate(
            "${VIDEO_ARTIST_SCREEN}/${ScreenArgs.TITLE}?" +
                    "${ScreenArgs.CATEGORY}=${
                        Gson().toJson(category)
                    }"
        ) {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navToArtistVideo(category: CategoryDtoItem, artist: ArtistDtoItem) {
        navController.navigate(
            "${ARTIST_VIDEOS_SCREEN}/${ScreenArgs.TITLE}?" +
                    "${ScreenArgs.CATEGORY}=${
                        Gson().toJson(category)
                    }&" +
                    "${ScreenArgs.CONTENT}=${Gson().toJson(artist)}"
        ) {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navToAlbumVideo(category: CategoryDtoItem, album: AlbumDtoItem) {
        navController.navigate(
            "${ALBUM_VIDEOS_SCREEN}/${ScreenArgs.TITLE}?" +
                    "${ScreenArgs.CATEGORY}=${
                        Gson().toJson(category)
                    }&" +
                    "${ScreenArgs.CONTENT}=${Gson().toJson(album)}"
        ) {
            launchSingleTop = true
            restoreState = true
        }

    }

    fun navToArtistVideoPlayer(category: CategoryDtoItem, track: TracksDtoItem) {
        navController.navigate(
            "${ARTIST_VIDEO_PLAYER_SCREEN}/${ScreenArgs.TITLE}?" +
                    "${ScreenArgs.CATEGORY}=${
                        Gson().toJson(category)
                    }&" +
                    "${ScreenArgs.CONTENT}=${Gson().toJson(track)}"
        )
    }


    fun navToAlbumVideoPlayer(category: CategoryDtoItem, track: AlbumTrackDtoItem) {
        navController.navigate(
            "${ALBUM_VIDEO_PLAYER_SCREEN}/${ScreenArgs.TITLE}?" +
                    "${ScreenArgs.CATEGORY}=${
                        Gson().toJson(category)
                    }&" +
                    "${ScreenArgs.CONTENT}=${Gson().toJson(track)}"
        )
    }

    fun navToAudioPlayer(category: CategoryDtoItem, track: TracksDtoItem) {
        navController.navigate(
            "${ARTIST_AUDIO_PLAYER_SCREEN}/${ScreenArgs.TITLE}?" +
                    "${ScreenArgs.CATEGORY}=${
                        Gson().toJson(category)
                    }&" +
                    "${ScreenArgs.CONTENT}=${Gson().toJson(track)}"
        ) {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navToAudioAlbum(category: CategoryDtoItem) {
        navController.navigate(
            "${AUDIO_ALBUM_SCREEN}/${ScreenArgs.TITLE}?" +
                    "${ScreenArgs.CATEGORY}=${
                        Gson().toJson(category)
                    }"
        ) {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navToVideoAlbum(category: CategoryDtoItem) {
        navController.navigate(
            "${VIDEO_ALBUM_SCREEN}/${ScreenArgs.TITLE}?" +
                    "${ScreenArgs.CATEGORY}=${
                        Gson().toJson(category)
                    }"
        ) {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navToAlbumAudioPlayer(category: CategoryDtoItem, track: AlbumTrackDtoItem) {
        navController.navigate(
            "${ALBUM_AUDIO_PLAYER_SCREEN}/${ScreenArgs.TITLE}?" + "${ScreenArgs.CATEGORY}=${
                Gson().toJson(
                    category
                )
            }&" + "${ScreenArgs.CONTENT}=${Gson().toJson(track)}"
        ) {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navToLoginForLogOut(mobile: String? = null) {
        navController.navigate("${LOGIN_SCREEN}/$mobile") {
            popUpTo(ScreenRoute.Main.route)
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navToUpdateProfile(profile: UserProfile) {
        navController.navigate(
            "${UPDATE_PROFILE_SCREEN}/${ScreenArgs.TITLE}?" + "${ScreenArgs.CONTENT}=${
                Gson().toJson(
                    profile
                )
            }"
        ) {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navToDownloads() {
        navController.navigate(DOWNLOADS_SCREEN) {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navToUserJourneyVideoPlayer(category: CategoryDtoItem) {
        navController.navigate(
            "${USER_JOURNEY_VIDEO_PLAYER_SCREEN}/${ScreenArgs.TITLE}?" +
                    "${ScreenArgs.CATEGORY}=${
                        Gson().toJson(category)
                    }"
        )
    }

}