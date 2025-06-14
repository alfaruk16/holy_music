package com.holymusic.app.features.presentation.otp

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.holymusic.app.R
import com.holymusic.app.core.components.AppBar
import com.holymusic.app.core.components.ButtonK
import com.holymusic.app.core.components.TextFieldK
import com.holymusic.app.core.theme.BackGroundColor
import com.holymusic.app.core.theme.BackGroundDark
import com.holymusic.app.core.theme.Gray
import com.holymusic.app.core.theme.GrayExtraLight
import com.holymusic.app.core.theme.GrayLight
import com.holymusic.app.core.theme.Primary
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.core.util.KeyboardUnFocusHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun OtpScreen(
    navController: NavHostController,
    viewModel: OtpViewModel = hiltViewModel(),
    scope: CoroutineScope = rememberCoroutineScope(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navToResetPassword: (String) -> Unit
) {
    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(id = R.string.verification),
                navController = navController
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { paddingValues ->

        val state = viewModel.state.collectAsStateWithLifecycle()
        val mobileFocusRequester = FocusRequester()

        KeyboardUnFocusHandler()

        if (state.value.showSnackBar) {
            scope.launch {
                viewModel.closeSnackBar()
                snackBarHostState.showSnackbar(state.value.message)
            }
        }

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {


            Column(Modifier.padding(start = 20.dp, end = 20.dp)) {

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.onBackground, shape = RoundedCornerShape(15.dp))
                ) {
                    Icon(
                        Icons.Filled.Lock, contentDescription = null,
                        modifier = Modifier
                            .padding(15.dp)
                            .size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    text = stringResource(id = R.string.enter_code),
                    fontSize = 20.sp, color = MaterialTheme.colorScheme.secondary,
                    style = Typography.titleLarge
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = state.value.phone,
                    color = MaterialTheme.colorScheme.onSecondary,
                    style = Typography.displaySmall
                )

                Spacer(modifier = Modifier.height(10.dp))

                TextFieldK(
                    value = state.value.otp,
                    onValueChange = { viewModel.mobileNumberChanged(it) },
                    focusRequester = mobileFocusRequester,
                    leadingIcon = {
                        Icon(Icons.Filled.Lock, contentDescription = null)
                    },
                    label = R.string.enter_otp,
                    keyboardType = KeyboardType.Phone,
                    error = if (state.value.isValidate && state.value.otp.length != 6) stringResource(
                        id = R.string.enter_valid_otp
                    ) + " (${state.value.otp.length}/6)" else "",
                    modifier = Modifier.padding(vertical = 6.dp)
                )




                Spacer(modifier = Modifier.height(20.dp))

                ButtonK(
                    text = R.string.submit,
                    isLoading = state.value.isLoading, isValid = state.value.valid
                ) {
                    viewModel.submit(navToResetPassword)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = stringResource(id = R.string.didnt_get_code), color = MaterialTheme.colorScheme.onSecondary,
                        style = Typography.displaySmall)
                    Text(
                        text = stringResource(id = R.string.resend),
                        color = if (state.value.time.isNotEmpty()) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            viewModel.resend()
                        },
                        style = Typography.titleSmall
                    )
                    if (state.value.time.isNotEmpty())
                        Text(text = state.value.time + " s", color = MaterialTheme.colorScheme.onSecondary,
                            style = Typography.displaySmall)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
