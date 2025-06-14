package com.holymusic.app.features.presentation.choose_plan

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.holymusic.app.MainActivity
import com.holymusic.app.R
import com.holymusic.app.core.components.AppBar
import com.holymusic.app.core.components.Loader
import com.holymusic.app.core.theme.BackGroundColor
import com.holymusic.app.core.theme.Gray
import com.holymusic.app.core.theme.Primary
import com.holymusic.app.core.util.Utils
import com.holymusic.app.features.data.remote.entity.Operators
import com.holymusic.app.features.presentation.choose_plan.components.Agree
import com.holymusic.app.features.presentation.choose_plan.components.AlreadyRegisterDialogue
import com.holymusic.app.features.presentation.choose_plan.components.BanglalinkPaymentDialogue
import com.holymusic.app.features.presentation.choose_plan.components.OnlinePayment
import com.holymusic.app.features.presentation.choose_plan.components.ProcessingDialogue
import com.holymusic.app.features.presentation.choose_plan.components.RobiPaymentDialogue
import com.holymusic.app.features.presentation.choose_plan.components.RobiPaymentStatusDialogue
import com.holymusic.app.features.presentation.choose_plan.components.OTPDialogue
import com.holymusic.app.features.presentation.choose_plan.components.TelcoPaymentStatusDialogue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.sequences.ifEmpty

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ChoosePlanScreen(
    navController: NavHostController,
    viewModel: ChoosePlanViewModel = hiltViewModel(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navToSSL: (String) -> Unit,
    scope: CoroutineScope = rememberCoroutineScope(),
    navToLogin: () -> Unit
) {
    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(id = R.string.choose_plan),
                navController = navController
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { paddingValues ->

        val state by viewModel.state.collectAsStateWithLifecycle()
        val width = LocalConfiguration.current.screenWidthDp

        val isBanglalink = remember {
            mutableStateOf(false)
        }

        val isRobi = remember {
            mutableStateOf(false)
        }

        if (isRobi.value)
            RobiPaymentDialogue(
                isRobi = viewModel.isRobi(state.user),
                user = state.user,
                mobile = state.mobile,
                mobileNumberChanged = viewModel::mobileNumberChanged,
                isValidate = state.isValidate,
                proceed = {
                    if (viewModel.isRobi(it)) {
                        isRobi.value = false
                        viewModel.initiateRobiPayment(it)
                    } else {
                        viewModel.validated()
                    }
                }
            ) {
                isRobi.value = false
            }

        if (!state.robiResponse.response.isNullOrEmpty()) {
            OTPDialogue(
                onDismiss = viewModel::dismissRobi,
                mobile = state.mobile.ifEmpty { state.user },
                pinNumberChanged = viewModel::pinNumberChanged,
                proceed = {
                    viewModel.verifyRobiOtp(it)
                },
                isValidate = state.isValidate,
                pin = state.pin
            )
        }

        if (!state.banglalinkResponseDto.response.isNullOrEmpty()) {
            OTPDialogue(
                onDismiss = viewModel::dismissBanglalink,
                mobile = state.mobile.ifEmpty { state.user },
                pinNumberChanged = viewModel::pinNumberChanged,
                proceed = {
                    viewModel.verifyBanglalinkOtp(it)
                },
                isValidate = state.isValidate,
                pin = state.pin
            )
        }

        if (isBanglalink.value)
            BanglalinkPaymentDialogue(
                isBanglalink = viewModel.isBanglalink(state.user),
                user = state.user,
                mobile = state.mobile,
                mobileNumberChanged = viewModel::mobileNumberChanged,
                isValidate = state.isValidate,
                proceed = {
                    if (viewModel.isBanglalink(it)) {
                        isBanglalink.value = false
                        viewModel.initiateBanglalinkPayment(it)
                    } else {
                        viewModel.validated()
                    }
                }
            ) {
                isBanglalink.value = false
            }

        if (state.showSnackBar) {
            scope.launch {
                viewModel.closeSnackBar()
                snackBarHostState.showSnackbar(state.error)
            }
        }

        if (state.failed.isNotEmpty()) {
            scope.launch {
                viewModel.closeFailedSnackBar()
                snackBarHostState.showSnackbar(state.failed)
            }
        }

        if (state.alreadyRegister.isNotEmpty())
            AlreadyRegisterDialogue(mobile = state.mobile, state.alreadyRegister) {
                viewModel.removeAlreadyRegister()
                viewModel.clearMobile()
            }

        if (state.paymentSuccessStatus)
            RobiPaymentStatusDialogue(mobile = state.mobile) {
                viewModel.removeRobiPaymentStatus()
                viewModel.clearMobile()
                navController.navigateUp()
            }

        if (state.processing)
            ProcessingDialogue(mobile = state.mobile) {
                viewModel.removeProcessingDialogue()
                viewModel.clearMobile()
            }

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(id = R.string.active_plan_to_unlock),
                fontSize = 18.sp,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(horizontal = 15.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row {
                PlanBenefit(text = R.string.largest_content, width = width)
                PlanBenefit(text = R.string.unlock_features, width = width)
            }
            Row {
                PlanBenefit(text = R.string.add_free, width = width)
                PlanBenefit(text = R.string.download_content, width = width)
            }

            Spacer(modifier = Modifier.height(5.dp))

            Agree(agree = state.agree, agreeChanged = viewModel::agreeChanged)

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = stringResource(id = R.string.choose_payment),
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 15.dp)
            )

            OnlinePayment(
                sections = listOf(
                    if (viewModel.isBanglalink(state.user))
                        PaymentOptions(
                            title = Utils.banglalink,
                            image = R.drawable.banglalink_logo,
                            icon = R.drawable.banglalink_icon,
                            plans = state.banglalinkPlans,
                            operator = Operators.Banglalink.name,
                            selected = {
                                if (MainActivity.isLoggedIn) {
                                    if (state.agree) {
                                        viewModel.planSelected(it)
                                        isBanglalink.value = true
                                    } else {
                                        scope.launch {
                                            viewModel.closeSnackBar()
                                            snackBarHostState.showSnackbar(state.error)
                                        }
                                    }
                                } else {
                                    navToLogin()
                                }
                            }
                        ) else PaymentOptions(
                        title = AnnotatedString(""),
                        icon = 0,
                        image = 0,
                        plans = emptyList(),
                        operator = "",
                        selected = {}),
                    if (viewModel.isRobi(state.user))
                        PaymentOptions(
                            title = Utils.robi,
                            image = R.drawable.robi_logo,
                            icon = R.drawable.robi_icon,
                            plans = state.robiPlans,
                            operator = Operators.Robi.name,
                            selected = {
                                if (MainActivity.isLoggedIn) {
                                    if (state.agree) {
                                        viewModel.planSelected(it)
                                        isRobi.value = true
                                    } else {
                                        scope.launch {
                                            viewModel.closeSnackBar()
                                            snackBarHostState.showSnackbar(state.error)
                                        }
                                    }
                                } else {
                                    navToLogin()
                                }
                            }
                        ) else PaymentOptions(
                        title = AnnotatedString(""),
                        icon = 0,
                        image = 0,
                        plans = emptyList(),
                        operator = "",
                        selected = {}),
                    PaymentOptions(
                        title = Utils.bKash,
                        image = R.drawable.bkash_logo,
                        icon = R.drawable.bkash_icon,
                        plans = state.bkashPlans,
                        operator = Operators.BKash.name,
                        selected = {
                            if (MainActivity.isLoggedIn) {
                                viewModel.planSelected(it)
                                viewModel.getBkashToken(navToSSL)
                            } else {
                                navToLogin()
                            }
                        }
                    ),
                    PaymentOptions(
                        title = Utils.nagad,
                        image = R.drawable.nagad_logo,
                        icon = R.drawable.nagad_icon,
                        plans = state.nagadPlans,
                        operator = Operators.Nagad.name,
                        selected = {
                            if (MainActivity.isLoggedIn) {
                                viewModel.planSelected(it)
                                viewModel.initiateNagadPay(navToSSL)
                            } else {
                                navToLogin()
                            }
                        }
                    ),
                    PaymentOptions(
                        title = Utils.mfs,
                        image = R.drawable.ssl,
                        icon = R.drawable.logo_small,
                        plans = state.sslPlans,
                        operator = Operators.SSL.name,
                        selected = {
                            if (MainActivity.isLoggedIn) {
                                viewModel.planSelected(it)
                                viewModel.initiateSSLPayment(navToSSL)
                            } else {
                                navToLogin()
                            }
                        }
                    ),
                    PaymentOptions(
                        title = Utils.amarPay,
                        image = R.drawable.amar_pay,
                        icon = R.drawable.logo_small,
                        plans = state.amarPayPlan,
                        operator = Operators.AmarPay.name,
                        selected = {
                            if (MainActivity.isLoggedIn) {
                                viewModel.planSelected(it)
                                viewModel.initiateAmarPayPayment(navToSSL)
                            } else {
                                navToLogin()
                            }
                        }
                    ),
                ),
                selectedPlan = state.selectedPlan,
                selectedPayment = state.selectedOperator,
                selectPayment = viewModel::selectPaymentMethod
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
        if (state.isLoading)
            Loader(paddingValues = paddingValues)
    }

}

@Composable
fun PlanBenefit(text: Int, width: Int) {
    Row(
        modifier = Modifier
            .padding(start = 15.dp, bottom = 5.dp)
            .width((width / 2).dp)
    ) {
        Icon(
            Icons.Filled.Check, contentDescription = null, tint = Primary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = stringResource(id = text), color = MaterialTheme.colorScheme.secondary)
    }
}