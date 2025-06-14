package com.holymusic.app.features.presentation.user_journey

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.holymusic.app.R
import com.holymusic.app.core.components.AppBar
import com.holymusic.app.core.components.VideoPlayer
import com.holymusic.app.core.theme.Primary
import com.holymusic.app.core.theme.TextGradientColor
import com.holymusic.app.core.theme.TextGradientColor2
import com.holymusic.app.core.theme.Typography
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

const val faceBookPage = "https://www.facebook.com/muslim.bd.co.official"

@SuppressLint("ContextCastToActivity")
@Composable
fun UserJourneyVideoPlayerScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val activity = LocalContext.current as Activity
    val systemUiController: SystemUiController = rememberSystemUiController()
    val uriHandler = LocalUriHandler.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppBar(
                title = "ব্যবহার বিধি",
                icon = R.drawable.user_journey,
                navController = navController,
                onBackPressed = {
                    activity.requestedOrientation =
                        ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    systemUiController.isStatusBarVisible = true
                }
            )
        }
    ) { paddingValues ->

        val height = LocalConfiguration.current.screenHeightDp
        val width = LocalConfiguration.current.screenWidthDp / 1.5

        Column(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.onTertiary, MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .background(color = Primary)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ব্যবহার বিধি (ভিডিও)",
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    style = Typography.titleLarge
                )
            }
            Box(
                modifier = Modifier
                    .height((height * 2 / 3).dp)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user_journey_banner),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
                VideoPlayer(
                    "https://v4technologies.sgp1.cdn.digitaloceanspaces.com/muslimbd/scholar/video/HT_UserJourney_LogIn.mp4",
                    {},
                    navController,
                    autoPlay = false
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .background(color = Primary)
                    .fillMaxWidth().padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "সাপোর্ট নম্বর : 88 88 01400 410188",
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp),
                    style = Typography.titleLarge
                )
                Text(
                    text = "ইমেইল : support@v4technologiesbd.com",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W500,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            UserJourneyItem(
                text = "রেজিস্ট্রেশন / একাউন্ট (নির্দেশনা)",
                images = listOf(R.drawable.sign_up_1, R.drawable.sign_up_2),
                width
            )
            UserJourneyItem(
                text = "লগইন নির্দেশনা",
                images = listOf(R.drawable.log_in_1, R.drawable.log_in_2, R.drawable.log_in_3),
                width
            )
            UserJourneyItem(
                text = "জি মেইল (G-MAIL) লগইন",
                images = listOf(
                    R.drawable.sign_up_google_1,
                    R.drawable.sign_up_google_2,
                    R.drawable.sign_up_google_3,
                    R.drawable.sign_up_google_4
                ),
                width
            )
//            UserJourneyItem(
//                text = stringResource(id = R.string.bkash),
//                images = listOf(
//                    R.drawable.bkash_payment1,
//                    R.drawable.bkash_payment2,
//                    R.drawable.bkash_payment3,
//                    R.drawable.bkash_payment4,
//                    R.drawable.bkash_payment5,
//                    R.drawable.bkash_payment6,
//                    R.drawable.bkash_payment7,
//                    R.drawable.bkash_payment8,
//                ),
//                width
//            )
        }
    }
}

@Composable
fun UserJourneyItem(text: String, images: List<Int>, width: Double) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(color = Primary)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                style = Typography.titleLarge
            )
        }

        LazyRow {
            items(count = images.size) {
                Image(
                    painter = painterResource(id = images[it]), contentDescription = null,
                    modifier = Modifier.width(width.dp),
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}
