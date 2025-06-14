package com.holymusic.app.features.presentation.login

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
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
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.core.util.Common
import com.holymusic.app.core.util.KeyboardUnFocusHandler
import com.holymusic.app.features.presentation.login.components.GoogleApiContract
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition", "ContextCastToActivity")
@Composable
fun LoginScreen(
    navController: NavHostController,
    navToSignUp: () -> Unit,
    navToChoosePlan: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
    scope: CoroutineScope = rememberCoroutineScope(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navToForgotPassword: () -> Unit
) {
    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(id = R.string.sign_in),
                navController = navController
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { paddingValues ->

        val state = viewModel.state.collectAsStateWithLifecycle()
        val width = LocalConfiguration.current.screenWidthDp
        val mobileFocusRequester = FocusRequester()
        val passwordFocusRequester = FocusRequester()
        val context: Context = LocalContext.current
        val activity: Activity = LocalContext.current as Activity
        val signInRequestCode = 1

        val authResultLauncher =
            rememberLauncherForActivityResult(contract = GoogleApiContract()) { task ->
                try {
                    val gsa = task?.getResult(ApiException::class.java)

                    if (gsa != null) {
                        viewModel.signInWithGoogle(gsa, navToChoosePlan, navController)
                    } else {
                        println("info not found")
                    }
                } catch (e: ApiException) {
                    println("Error in AuthScreen%s $e")
                }
            }

        val callbackManager = remember {
            CallbackManager.Factory.create()
        }
        val fbLauncher = rememberLauncherForActivityResult(
            LoginManager.getInstance().createLogInActivityResultContract(callbackManager)
        ) { result ->

            LoginManager.getInstance().onActivityResult(
                result.resultCode,
                result.data,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult) {
                        println("onSuccess $result")
                    }

                    override fun onCancel() {
                        println("onCancel")
                    }

                    override fun onError(error: FacebookException) {
                        println("onError $error")
                    }
                }
            )
        }

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
                    .padding(horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Column(
                    Modifier.padding(start = 10.dp, end = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(30.dp))
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .size((width / 3).dp)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = stringResource(id = R.string.welcome_to),
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.primary,
                        style = Typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(text = stringResource(id = R.string.sign_in_your_account),
                            style = Typography.displaySmall,
                            color = MaterialTheme.colorScheme.secondary)

                        Text(
                            text = stringResource(id = R.string.create_an_account_first),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                navToSignUp()
                            },
                            style = Typography.titleSmall
                        )
                    }

                    TextFieldK(
                        value = state.value.mobile,
                        onValueChange = { viewModel.mobileNumberChanged(it) },
                        focusRequester = mobileFocusRequester,
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
                        label = R.string.mobile_number,
                        keyboardType = KeyboardType.Phone,
                        error = if (state.value.isValidate && !Common.isValidMobile(state.value.mobile)) stringResource(
                            id = R.string.enter_valid_mobile
                        ) + " (${state.value.mobile.length}/11)" else "",
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    TextFieldK(
                        value = state.value.password,
                        onValueChange = { viewModel.passwordChanged(it) },
                        focusRequester = passwordFocusRequester,
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                        label = R.string.password,
                        visualTransformation = if (state.value.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        error = if (state.value.isValidate && state.value.password.isEmpty()) stringResource(
                            id = R.string.give_password
                        ) else "",
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

                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(modifier = Modifier.padding(horizontal = 10.dp)) {
                    ButtonK(
                        text = R.string.sign_in,
                        isLoading = state.value.isLoading,
                        isValid = state.value.valid
                    ) {
                        viewModel.loginWithUserName(
                            mobileFocusRequester = mobileFocusRequester,
                            passwordFocusRequester = passwordFocusRequester,
                            navToChoosePlan = navToChoosePlan,
                            navController = navController
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 10.dp)
                ) {
                    Checkbox(
                        checked = state.value.rememberMe,
                        onCheckedChange = { viewModel.rememberMeChanged(it) },
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                    )

                    Text(text = stringResource(id = R.string.remember_me),
                        style = Typography.displaySmall,
                        color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = stringResource(id = R.string.is_forgot_password), color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            navToForgotPassword()
                        },
                        style = Typography.displaySmall)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                    Text(
                        text = stringResource(id = R.string.dont_have_account),
                        style = Typography.displaySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    ButtonK(
                        text = R.string.create_an_account_and_password,
                        backGroundColor = Color.Transparent,
                        borderColor = MaterialTheme.colorScheme.onSecondary,
                        textColor = MaterialTheme.colorScheme.primary
                    ) {
                        navToSignUp()
                    }


                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(id = R.string.othoba),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = Typography.displaySmall,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    ButtonK(
                        text = R.string.sign_in_with_google,
                        borderColor = MaterialTheme.colorScheme.onSecondary,
                        backGroundColor = Color.Transparent,
                        prefixIcon = R.drawable.ic_google_logo,
                        textColor = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Normal
                    ) {
                        authResultLauncher.launch(signInRequestCode)
                    }
                }

//                Spacer(modifier = Modifier.height(10.dp))
//                FacebookLoginButton {
//                    fbLauncher.launch(listOf("email", "business_management"))
//                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
        if (state.value.isLoading)
            Loader(paddingValues = paddingValues)
    }
}
