package com.holymusic.app.features.presentation.sign_up

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.holymusic.app.R
import com.holymusic.app.core.components.AppBar
import com.holymusic.app.core.components.ButtonK
import com.holymusic.app.core.components.Loader
import com.holymusic.app.core.components.TextFieldK
import com.holymusic.app.core.theme.Primary
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.core.util.Common
import com.holymusic.app.core.util.KeyboardUnFocusHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

const val termsAndConditions = "http://tnc.techmatrixlab.com/"

@SuppressLint("CoroutineCreationDuringComposition", "ContextCastToActivity")
@Composable
fun SignUpScreen(
    navController: NavHostController,
    navToChoosePlan: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel(),
    scope: CoroutineScope = rememberCoroutineScope(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(id = R.string.create_an_account),
                navController = navController
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { paddingValues ->

        val state = viewModel.state.collectAsStateWithLifecycle()
        val width = LocalConfiguration.current.screenWidthDp
        val mobileFocusRequester = FocusRequester()
        val passwordFocusRequester = FocusRequester()
        val confirmPasswordFocusRequester = FocusRequester()
        val context: Context = LocalContext.current
        val activity: Activity = LocalContext.current as Activity
        val uriHandler = LocalUriHandler.current

        KeyboardUnFocusHandler()

        if (state.value.showSnackBar) {
            scope.launch {
                viewModel.closeSnackBar()
                snackBarHostState.showSnackbar(state.value.message)
            }
        }

        val settingResultRequest = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult()
        ) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK)
                viewModel.getLocation(context, activity)
            else {
                println("Denied")
            }
        }

        val locationPermissionResultLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = {
                if (it) {
                    viewModel.checkLocationSetting(
                        context = context,
                        activity = activity,
                        onDisabled = { intentSenderRequest ->
                            settingResultRequest.launch(intentSenderRequest)
                        },
                        onEnabled = { viewModel.getLocation(context, activity) }
                    )

                }
            }
        )

        LaunchedEffect(Unit) {
            locationPermissionResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.background)
        ) {

//            Image(
//                painter = painterResource(id = R.drawable.pattern),
//                contentDescription = null,
//                modifier = Modifier
//                    .width(width.dp)
//                    .height(width.dp),
//            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(30.dp))

                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(RoundedCornerShape(15.dp))
                            .size((width / 3).dp)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = stringResource(id = R.string.create_an_account),
                        fontSize = 24.sp,
                        color = Primary,
                        style = Typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    TextFieldK(
                        value = state.value.mobile,
                        label = R.string.mobile_number,
                        focusRequester = mobileFocusRequester,
                        onValueChange = { viewModel.mobileChanged(it) },
                        keyboardType = KeyboardType.Phone,
                        leadingIcon = {
                            Row(
                                modifier = Modifier.padding(start = 15.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Phone, contentDescription = null)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "88 ",
                                    fontSize = 20.sp,
                                    style = Typography.displayLarge
                                )
                            }
                        },
                        error = if (state.value.isValidate && !Common.isValidMobile(state.value.mobile)
                        ) stringResource(
                            id = R.string.enter_valid_mobile
                        ) + " (${state.value.mobile.length}/11)" else "",
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

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
                        visualTransformation = if (state.value.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.padding(vertical = 6.dp),
                        suffixIcon = {
                            Icon(if (state.value.showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .clickable {
                                        viewModel.showPassword()
                                    })
                        }
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
                            id = R.string.re_enter_password_error
                        )
                        else if (state.value.isValidate && state.value.password != state.value.confirmPassword) stringResource(
                            id = R.string.password_didnt_match
                        )
                        else "",
                        visualTransformation = if (state.value.showConPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.padding(vertical = 6.dp),
                        suffixIcon = {
                            Icon(if (state.value.showConPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .clickable {
                                        viewModel.showConPassword()
                                    })
                        }
                    )

                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 20.dp)
                ) {
                    Checkbox(
                        checked = state.value.agree,
                        onCheckedChange = { viewModel.agreeChanged(it) },
                        colors = CheckboxDefaults.colors(checkedColor = Primary)
                    )
                    Text(text = stringResource(id = R.string.agree_with),
                        style = Typography.displaySmall,
                        color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = stringResource(id = R.string.holy_tune_terms), color = Primary,
                        modifier = Modifier.clickable {
                            uriHandler.openUri(termsAndConditions)
                        }, textDecoration = TextDecoration.Underline,
                        style = Typography.displaySmall
                    )
                }

                Column {
                    ButtonK(
                        text = R.string.create_an_account,
                        isValid = state.value.valid,
                        isLoading = state.value.isLoading,
                    ) {
                        viewModel.signUp(
                            navToChoosePlan,
                            mobileFocusRequester,
                            passwordFocusRequester,
                            confirmPasswordFocusRequester,
                            navController
                        )
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        text = stringResource(id = R.string.already_have_an_account),
                        style = Typography.displaySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.padding(bottom = 5.dp))
                    ButtonK(text = R.string.sign_in, textColor = Primary, backGroundColor = Color.Transparent,
                        borderColor = MaterialTheme.colorScheme.onSecondary
                    ) {
                        navController.popBackStack()
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
        if (state.value.isLoading)
            Loader(paddingValues = paddingValues)
    }
}
