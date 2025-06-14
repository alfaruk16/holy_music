package com.holymusic.app.features.presentation.reset_password

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
import com.holymusic.app.core.theme.GrayLight
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.core.util.KeyboardUnFocusHandler
import com.holymusic.app.features.presentation.reset_password.components.SuccessDialogue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ResetPasswordScreen(
    navController: NavHostController,
    viewModel: ResetPasswordViewModel = hiltViewModel(),
    navToLogin: (String) -> Unit,
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope()
    ) {
    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(id = R.string.reset_password),
                navController = navController
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { paddingValues ->

        val state = viewModel.state.collectAsStateWithLifecycle()
        val passwordFocusRequester = FocusRequester()
        val confirmPasswordFocusRequester = FocusRequester()

        KeyboardUnFocusHandler()

        if (state.value.showDialogue) {
            SuccessDialogue(onDismiss = {
                navToLogin(state.value.mobile)
            })
        }

        if(state.value.showError){
            scope.launch {
                viewModel.closeSnackBar()
                snackBarHostState.showSnackbar(state.value.message)
            }
        }

        BackHandler {
            navToLogin(state.value.mobile)
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
                    text = stringResource(id = R.string.reset_password),
                    fontSize = 20.sp, color = MaterialTheme.colorScheme.secondary,
                    style = Typography.titleLarge
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = stringResource(id = R.string.reset_password_description),
                    color = MaterialTheme.colorScheme.onSecondary,
                    style = Typography.displaySmall
                )

                Spacer(modifier = Modifier.height(10.dp))

                TextFieldK(
                    value = state.value.password,
                    label = R.string.enter_password,
                    focusRequester = passwordFocusRequester,
                    onValueChange = { viewModel.passwordChanged(it) },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                    error = if (state.value.isValidate && state.value.password.isEmpty()) stringResource(
                        id = R.string.enter_password
                    ) else if (state.value.isValidate && state.value.password.length < 6) stringResource(
                        id = R.string.password_should_be
                    ) else "",
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                TextFieldK(
                    value = state.value.confirmPassword,
                    label = R.string.re_enter_password,
                    focusRequester = confirmPasswordFocusRequester,
                    onValueChange = { viewModel.confirmPasswordChanged(it) },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                    error = if (state.value.isValidate && state.value.confirmPassword.isEmpty()) stringResource(
                        id = R.string.re_enter_password
                    ) else if (state.value.isValidate && state.value.confirmPassword.length < 6) stringResource(
                        id = R.string.password_should_be
                    )
                    else if (state.value.isValidate && state.value.password != state.value.confirmPassword) stringResource(
                        id = R.string.password_didnt_match
                    )
                    else "",
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.padding(vertical = 6.dp)
                )



                Spacer(modifier = Modifier.height(20.dp))

                ButtonK(
                    text = R.string.reset_password,
                    isValid = state.value.valid,
                    isLoading = state.value.isLoading
                ) {
                    viewModel.submit()
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

        }
    }
}
