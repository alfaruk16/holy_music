package com.holymusic.app.features.presentation.my_plans

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.holymusic.app.R
import com.holymusic.app.core.components.AppBar
import com.holymusic.app.core.components.Loader
import com.holymusic.app.core.theme.BackGroundColor
import com.holymusic.app.core.theme.BackGroundDark
import com.holymusic.app.core.theme.Gray
import com.holymusic.app.core.theme.GrayLight
import com.holymusic.app.core.theme.Orange
import com.holymusic.app.core.theme.Primary
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.core.util.Enums
import com.holymusic.app.features.data.remote.model.SubStatusDtoItem
import com.holymusic.app.features.presentation.my_plans.components.CancelDialogue
import com.holymusic.app.features.presentation.my_plans.components.RobiCancelDialogue
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

const val monthly = "MONTHLY"
const val halfYearly = "HALF YEARLY"
const val yearly = "YEARLY"
const val weekly = "WEEKLY"


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MyPlanScreen(
    navController: NavHostController,
    viewModel: MyPlanViewModel = hiltViewModel(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(id = R.string.subscribed),
                navController = navController
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { paddingValues ->

        val state by viewModel.state.collectAsStateWithLifecycle()

        val cancelPlan = remember {
            mutableStateOf(SubStatusDtoItem())
        }

        val isCancel = remember {
            mutableStateOf(false)
        }

        if (isCancel.value)
            CancelDialogue(onDismiss = { isCancel.value = false }) {
                isCancel.value = false
                viewModel.cancelPlan(navController, cancelPlan.value)
            }

        if(state.robiCancelCode.isNotEmpty())
            RobiCancelDialogue(mobile = state.user, code = state.robiCancelCode) {
                viewModel.closeRobiCancelDialogue()
            }

        LazyColumn(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
            content = {

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
                item {
                    Text(
                        text = stringResource(id = R.string.enjoy),
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(horizontal = 15.dp),
                        style = Typography.titleLarge
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    PlanBenefit(text = R.string.largest_content)
                }
                item {
                    PlanBenefit(text = R.string.unlock_features)
                }
                item {
                    PlanBenefit(text = R.string.add_free)
                }
                item {
                    PlanBenefit(
                        text = R.string.download_content
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(state.myPlans.size) {
                    val item = state.myPlans[it]
                    if (item.regstatus == Enums.Subscriptions.Subscribed.name)
                        Plan(item, cancel = { plan ->
                            cancelPlan.value = plan
                            isCancel.value = true
                        })
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }

            })

        if (state.isLoading)
            Loader(paddingValues = paddingValues)
    }
}

@Composable
fun PlanBenefit(text: Int) {
    Row(modifier = Modifier.padding(horizontal = 15.dp, vertical = 3.dp)) {
        Icon(
            Icons.Filled.Check, contentDescription = null, tint = Primary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = stringResource(id = text),
            style = Typography.displaySmall)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Plan(item: SubStatusDtoItem, cancel: (SubStatusDtoItem) -> Unit) {
    val expire = remember {
        try {
            LocalDate.parse(
                item.lastupdate,
                DateTimeFormatter.ofPattern("M/d/yyyy h:m:s a", Locale.ENGLISH)
            )
                .plusDays(if (item.frequency == monthly) 30 else if (item.frequency == halfYearly) 182 else if (item.frequency == yearly) 365 else if (item.frequency == weekly) 7 else 0)
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        } catch (e: Exception) {
            ""
        }
    }
    Box(contentAlignment = Alignment.TopEnd, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(horizontal = 15.dp, vertical = 8.dp)
                .background(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(15.dp))
                .border(width = 2.dp, color = Primary, shape = RoundedCornerShape(15.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(18.dp)
            ) {
                Text(
                    text = item.servicename ?: "",
                    color = MaterialTheme.colorScheme.secondary,
                    style = Typography.titleMedium
                )
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    text = "Expire: $expire",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
            Column {
                Text(
                    text = "${item.chargeAmount} BDT",
                    color = Primary,
                    fontSize = 20.sp,
                    style = Typography.titleLarge
                )
                Spacer(modifier = Modifier.height(7.dp))
                Box(
                    modifier = Modifier
                        .background(color = Color.Transparent, shape = RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clip(shape = RoundedCornerShape(8.dp))
                        .clickable {
                            cancel(item)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.cancel_plan),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 5.dp, horizontal = 8.dp),
                        style = Typography.titleMedium
                    )
                }
            }
            Spacer(modifier = Modifier.width(18.dp))
        }
        Box(modifier = Modifier.padding(end = 5.dp)) {
            Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.onBackground, shape = CircleShape)) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier
                        .padding(5.dp)
                        .size(18.dp)
                )
            }
        }

    }
}