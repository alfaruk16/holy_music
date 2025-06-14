package com.holymusic.app.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.holymusic.app.MainActivity
import com.holymusic.app.R
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.core.util.AppConstants
import com.holymusic.app.features.data.remote.model.CategoryDtoItem

@Composable
fun Drawer(
    mobile: String,
    closeDrawer: () -> Unit,
    navToArtist: (CategoryDtoItem) -> Unit,
    navToAudio: (CategoryDtoItem) -> Unit,
    navToVideo: (CategoryDtoItem) -> Unit,
    navToAlbum: (CategoryDtoItem) -> Unit,
    navToVideoAlbum: (CategoryDtoItem) -> Unit,
    navToMyFavorites: () -> Unit,
    navToDownloads: (CategoryDtoItem) -> Unit,
    navToLogin: () -> Unit,
    navToChoosePlan: () -> Unit,
    plan: String,
    navToMyPlan: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = null,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .clickable {
                        closeDrawer()
                    },
                tint = MaterialTheme.colorScheme.primary
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo), contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )
            }
        }
        if (MainActivity.isLoggedIn)
            Text(text = mobile, modifier = Modifier.padding(bottom = 8.dp), color = MaterialTheme.colorScheme.primary,
                style = Typography.displaySmall)

        if (MainActivity.isLoggedIn)
            Box(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .clickable {
                        if (MainActivity.isPremium.value) {
                            navToMyPlan()
                        } else {
                            navToChoosePlan()
                        }
                    },
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(id = if (MainActivity.isPremium.value) R.string.my_plan else R.string.get_premium),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            style = Typography.displaySmall
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = if (MainActivity.isPremium.value) plan else stringResource(id = R.string.subscribe),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            style = Typography.displaySmall
                        )
                    }
                    Icon(
                        painter = painterResource(id = R.drawable.subscribe),
                        contentDescription = null,
                        modifier = Modifier.size(45.dp),
                        tint = if (MainActivity.isPremium.value) MaterialTheme.colorScheme.scrim else MaterialTheme.colorScheme.primaryContainer.copy(
                            alpha = .5f
                        )
                    )
                }
            }
        else Text(
            text = stringResource(id = R.string.log_in),
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.clickable { navToLogin() },
            style = Typography.titleSmall
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

            DrawerItem(
                icon = R.drawable.artist,
                title = AppConstants.gajalArtist
            ) {
                navToArtist(
                    CategoryDtoItem(
                        name = AppConstants.gajalArtist,
                        icon = R.drawable.artist
                    )
                )
            }

            DrawerItem(
                icon = R.drawable.audio,
                title = AppConstants.gajalAudio
            ) {
                navToAudio(
                    CategoryDtoItem(
                        name = AppConstants.gajalAudio,
                        icon = R.drawable.artist
                    )
                )
            }
            DrawerItem(
                icon = R.drawable.video,
                title = AppConstants.gajalVideo
            ) {
                navToVideo(
                    CategoryDtoItem(
                        name = AppConstants.gajalVideo,
                        icon = R.drawable.video
                    )
                )
            }
            DrawerItem(
                icon = R.drawable.audio_album,
                title = AppConstants.audioAlbum
            ) {
                navToAlbum(
                    CategoryDtoItem(
                        name = AppConstants.audioAlbum,
                        icon = R.drawable.audio_album
                    )
                )
            }
            DrawerItem(
                icon = R.drawable.video_album,
                title = AppConstants.videoAlbum
            ) {
                navToVideoAlbum(
                    CategoryDtoItem(
                        name = AppConstants.videoAlbum,
                        icon = R.drawable.video_album
                    )
                )
            }

            DrawerItem(
                icon = R.drawable.audio,
                title = AppConstants.favorite_gajal
            ) {
                navToAudio(
                    CategoryDtoItem(
                        name = AppConstants.favorite_gajal,
                        icon = R.drawable.audio,
                        isFavorite = true
                    )
                )
            }

            DrawerItem(
                icon = R.drawable.video,
                title = AppConstants.favorite_gajalVideo
            ) {
                navToVideo(
                    CategoryDtoItem(
                        name = AppConstants.favorite_gajalVideo,
                        icon = R.drawable.video,
                        isFavorite = true
                    )
                )
            }
            DrawerItem(
                icon = R.drawable.baseline_favorite_24,
                title = AppConstants.myFavorites
            ) {
                navToMyFavorites()
            }
            DrawerItem(
                icon = R.drawable.downloaded,
                title = AppConstants.downloads
            ) {
                navToDownloads(
                    CategoryDtoItem(
                        name = AppConstants.downloads,
                        icon = R.drawable.downloaded
                    )
                )
            }
        }
    }
}

@Composable
fun DrawerItem(icon: Int, title: String, press: () -> Unit) {
    Box(modifier = Modifier.clickable { press() }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 5.dp)
        ) {
            Icon(
                painter = painterResource(id = icon), contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = title, color = MaterialTheme.colorScheme.secondary,
                style = Typography.displaySmall)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
        }
    }
}
