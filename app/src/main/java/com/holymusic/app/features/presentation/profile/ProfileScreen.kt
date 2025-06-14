package com.holymusic.app.features.presentation.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.holymusic.app.MainActivity
import com.holymusic.app.R
import com.holymusic.app.core.components.AppBar
import com.holymusic.app.core.theme.BackGroundDark
import com.holymusic.app.core.theme.Golden
import com.holymusic.app.core.theme.Gray
import com.holymusic.app.core.theme.GrayLight
import com.holymusic.app.core.theme.Primary
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.features.data.remote.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(
    navController: NavController = rememberNavController(),
    viewModel: ProfileViewModel = hiltViewModel(),
    scope: CoroutineScope = rememberCoroutineScope(),
    navToUpdateProfile: (UserProfile) -> Unit,
    navToMyPlan: () -> Unit,
    navToDownloads: () -> Unit,
    navToMyFavorite: () -> Unit,
    navToChoosePlan: () -> Unit
) {

    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(id = R.string.profile),
                navController = navController
            )
        }
    ) { paddingValues ->

        val state by viewModel.state.collectAsStateWithLifecycle()
        val width = (LocalConfiguration.current.screenWidthDp / 1.75).dp

        LaunchedEffect(Unit) {
            viewModel.getProfile()
            viewModel.checkSubscriptions()
        }

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Primary,
                        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                    )
                    .height(width),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (!state.profile.data?.imageUrl.isNullOrEmpty())
                    AsyncImage(
                        model = state.profile.data?.imageUrl, contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Fit
                    )
                else
                    Box(
                        modifier = Modifier.background(
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = CircleShape
                        )
                    ) {
                        Icon(
                            Icons.Filled.Person, contentDescription = null, modifier = Modifier
                                .size(80.dp)
                                .padding(10.dp),
                            tint = Primary
                        )
                    }
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = state.profile.data?.userFullName ?: "", fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    style = Typography.displayLarge
                )
            }

            Column(
                modifier = Modifier.verticalScroll(
                    rememberScrollState()
                )
            ) {
                Card(
                    modifier = Modifier
                        .padding(
                            top = (width - 40.dp),
                            start = 20.dp,
                            end = 20.dp
                        )
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    elevation = CardDefaults.cardElevation(5.dp)
                ) {
                    Row {
                        Column(
                            modifier = Modifier
                                .padding(15.dp)
                                .weight(1f)
                        ) {
                            Text(text = stringResource(id = R.string.full_name), color = MaterialTheme.colorScheme.onSecondary)
                            Text(
                                text = state.profile.data?.userFullName ?: "",
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 20.sp,
                                style = Typography.displayLarge
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = stringResource(id = R.string.mobile_number),
                                color = MaterialTheme.colorScheme.onSecondary,
                                style = Typography.displaySmall
                            )
                            Text(
                                text = state.profile.data?.mobileNo ?: "",
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 20.sp,
                                style = Typography.displayLarge
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = stringResource(id = R.string.gender),
                                color = MaterialTheme.colorScheme.onSecondary,
                                style = Typography.displaySmall
                            )
                            Text(
                                text = state.profile.data?.gender ?: "",
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 20.sp,
                                style = Typography.displayLarge
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = stringResource(id = R.string.date_of_birth),
                                color = MaterialTheme.colorScheme.onSecondary,
                                style = Typography.displaySmall
                            )
                            if (state.profile.data?.birthDate != null)
                                Text(
                                    text = LocalDate.parse(
                                        state.profile.data?.birthDate?.replace("T", " "),
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                    ).format(
                                        DateTimeFormatter.ofPattern("dd MMMM yyyy")
                                    ) ?: "",
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontSize = 20.sp,
                                    style = Typography.displayLarge
                                )
                        }

                        Column(modifier = Modifier.padding(10.dp)) {
                            Card(
                                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
                                elevation = CardDefaults.cardElevation(5.dp),
                                modifier = Modifier
                                    .width(100.dp)
                                    .clickable { navToDownloads() }
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Icon(
                                        Icons.Filled.Download,
                                        tint = MaterialTheme.colorScheme.primaryContainer,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(45.dp)
                                            .background(Primary, shape = CircleShape)
                                            .padding(10.dp)
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = stringResource(id = R.string.downloads),
                                        color = MaterialTheme.colorScheme.secondary,
                                        style = Typography.titleSmall
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(
                                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
                                elevation = CardDefaults.cardElevation(5.dp),
                                modifier = Modifier
                                    .width(100.dp)
                                    .clickable { navToMyFavorite() }
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Icon(
                                        Icons.Filled.FavoriteBorder,
                                        tint = MaterialTheme.colorScheme.primaryContainer,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(45.dp)
                                            .background(Primary, shape = CircleShape)
                                            .padding(10.dp)
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = stringResource(id = R.string.favorites),
                                        color = MaterialTheme.colorScheme.secondary,
                                        style = Typography.titleSmall
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .background(color = Primary, shape = RoundedCornerShape(8.dp))
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
                                style = Typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = if (MainActivity.isPremium.value) state.plan else stringResource(id = R.string.subscribe),
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
                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.padding(horizontal = 20.dp)) {

                    Card(
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
                        elevation = CardDefaults.cardElevation(5.dp),
                        modifier = Modifier
                            .clickable { navToUpdateProfile(state.profile.data ?: UserProfile()) }
                            .weight(.33f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Icon(
                                Icons.Filled.PersonOutline,
                                tint = MaterialTheme.colorScheme.primaryContainer,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(Primary, shape = CircleShape)
                                    .padding(5.dp)
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = stringResource(id = R.string.edit_profile),
                                color = MaterialTheme.colorScheme.secondary,
                                style = Typography.titleSmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Card(
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
                        elevation = CardDefaults.cardElevation(5.dp),
                        modifier = Modifier
                            .clickable {
                                scope.launch {
                                    viewModel.logOut()
                                    navController.navigateUp()
                                }
                            }
                            .weight(.33f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Icon(
                                Icons.AutoMirrored.Filled.Logout,
                                tint = Color.Yellow,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(Primary, shape = CircleShape)
                                    .padding(5.dp)
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = stringResource(id = R.string.log_out),
                                color = MaterialTheme.colorScheme.secondary,
                                style = Typography.titleSmall
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}