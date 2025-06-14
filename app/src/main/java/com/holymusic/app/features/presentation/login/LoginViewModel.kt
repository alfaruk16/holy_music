package com.holymusic.app.features.presentation.login

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.result.IntentSenderRequest
import androidx.compose.ui.focus.FocusRequester
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.holymusic.app.MainActivity
import com.holymusic.app.core.util.Async
import com.holymusic.app.core.util.Common
import com.holymusic.app.core.util.Enums
import com.holymusic.app.core.util.LocalConstant
import com.holymusic.app.features.data.remote.entity.Login
import com.holymusic.app.features.data.remote.entity.SubStatus
import com.holymusic.app.features.domain.use_case.GetSubscriptionsUseCase
import com.holymusic.app.features.domain.use_case.LoginUseCase
import com.holymusic.app.features.presentation.ScreenArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application,
    private val loginUseCase: LoginUseCase,
    savedStateHandle: SavedStateHandle,
    private val getSubscriptionsUseCase: GetSubscriptionsUseCase
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private var fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)
    private var preferences: SharedPreferences =
        application.getSharedPreferences(LocalConstant.sharedPreferences, MODE_PRIVATE)
    private val mobile: String = checkNotNull(savedStateHandle[ScreenArgs.TITLE] ?: "")

    init {
        _state.value = state.value.copy(mobile = mobile)
        getUniqueId(application.baseContext)
        viewModelScope.launch {
            getFirebaseToken()
        }
    }

    fun checkSignedInUser(applicationContext: Context) {
        val gsa = GoogleSignIn.getLastSignedInAccount(applicationContext)

        if (gsa != null) {
            println(gsa)
        }
    }

    fun checkLocationSetting(
        context: Context,
        activity: Activity,
        onDisabled: (IntentSenderRequest) -> Unit,
        onEnabled: () -> Unit
    ) {

        val locationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest
            .Builder()
            .addLocationRequest(locationRequest)

        val gpsSettingTask: Task<LocationSettingsResponse> =
            client.checkLocationSettings(builder.build())

        gpsSettingTask.addOnSuccessListener { getLocation(context, activity) }
        gpsSettingTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest = IntentSenderRequest
                        .Builder(exception.resolution)
                        .build()
                    onDisabled(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // ignore here
                }
            }
        }

    }

    fun getLocation(context: Context, activity: Activity) {

        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }

        val task = fusedLocationProviderClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            null
        )
        task.addOnSuccessListener {
            if (it != null) {
                _state.value = state.value.copy(
                    latitude = it.latitude,
                    longitude = it.longitude
                )
            }
        }
    }

    private suspend fun getFirebaseToken() {
        try {
            val task = FirebaseMessaging.getInstance().token.await()
            _state.value = state.value.copy(fcmDeviceId = task ?: "")
        } catch (e: Exception) {
            println(e.message)
        }
    }

    @SuppressLint("HardwareIds")
    private fun getUniqueId(baseContext: Context) {
        viewModelScope.launch {
            val androidId = Settings.Secure.getString(
                baseContext.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            _state.value = state.value.copy(
                appDeviceId = androidId,
                deviceInfo = Build.MANUFACTURER + " " + Build.MODEL + " " + Build.USER
            )
        }
    }

    fun mobileNumberChanged(mobile: String) {
        _state.value = state.value.copy(mobile = mobile)
        validate()
    }

    fun passwordChanged(password: String) {
        _state.value = state.value.copy(password = password)
        validate()
    }

    fun rememberMeChanged(remember: Boolean) {
        _state.value = state.value.copy(rememberMe = remember)
    }

    private fun validate() {
        if (Common.isValidMobile(state.value.mobile)
            && state.value.password.isNotEmpty()
        ) {
            _state.value = state.value.copy(valid = true)
        } else {
            _state.value = state.value.copy(valid = false)
        }
    }

    fun loginWithUserName(
        mobileFocusRequester: FocusRequester,
        passwordFocusRequester: FocusRequester,
        navToChoosePlan: () -> Unit,
        navController: NavController

    ) {
        _state.value = state.value.copy(isValidate = true)
        if (!Common.isValidMobile(state.value.mobile)
        ) {
            mobileFocusRequester.requestFocus()
        } else if (state.value.password.isEmpty()) {
            passwordFocusRequester.requestFocus()
        } else if (!state.value.isLoading) {
            login(
                Login(
                    AppDeviceId = state.value.appDeviceId,
                    City = state.value.city,
                    Country = state.value.country,
                    CountryCode = state.value.countryCode,
                    DeviceInfo = state.value.deviceInfo,
                    FcmDeviceId = state.value.fcmDeviceId.ifEmpty { state.value.appDeviceId },
                    Latitude = state.value.latitude,
                    LoginCode = state.value.loginCode,
                    Longitude = state.value.longitude,
                    Password = state.value.password,
                    RegisterWith = "M",
                    TelcoProvider = Common.getTelcoProvider(state.value.mobile),
                    UserName = "88" + state.value.mobile
                ),
                navToChoosePlan,
                navController
            )
        }
    }


    private fun login(login: Login, navToChoosePlan: () -> Unit, navController: NavController) {

        loginUseCase.invoke(login).onEach { result ->

            when (result) {
                is Async.Success -> {
                    val editor: SharedPreferences.Editor = preferences.edit()
                    editor.putString(LocalConstant.token, result.data?.data?.token)
                    editor.putString(LocalConstant.mobile, login.UserName)
                    editor.apply()

                    MainActivity.token = ("Bearer " + result.data?.data?.token)
                    MainActivity.isLoggedIn = true

                    checkSubscriptionStatus(
                        navController,
                        result.data?.message,
                        navToChoosePlan
                    )
                }

                is Async.Loading ->
                    _state.value = state.value.copy(isLoading = true)

                is Async.Error ->
                    _state.value = state.value.copy(
                        isLoading = false,
                        showSnackBar = true,
                        message = result.message ?: "Unknown error"
                    )

            }
        }.launchIn(viewModelScope)
    }

    fun closeSnackBar() {
        _state.value = state.value.copy(showSnackBar = false)
    }

    private fun checkSubscriptionStatus(
        navController: NavController,
        message: String?,
        navToChoosePlan: () -> Unit
    ) {

        if (MainActivity.isLoggedIn) {

            getSubscriptionsUseCase.invoke(
                body = SubStatus(
                    MSISDN = preferences.getString(LocalConstant.mobile, "") ?: ""
                )
            ).onEach { result ->

                when (result) {
                    is Async.Success -> {
                        _state.value = state.value.copy(
                            showSnackBar = true,
                            message = message ?: ""
                        )
                        if (!result.data.isNullOrEmpty()) {
                            for (item in result.data) {
                                if (item.regstatus == Enums.Subscriptions.Subscribed.name) {
                                    MainActivity.isPremium.value = true
                                    val editor: SharedPreferences.Editor = preferences.edit()
                                    editor.putBoolean(
                                        LocalConstant.isPremium,
                                        MainActivity.isPremium.value
                                    )
                                    editor.apply()
                                    break
                                }
                            }
                        }
                        delay(1000)
                        navController.navigateUp()
                    }

                    is Async.Error -> {
                        delay(1000)
                        navController.navigateUp()
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        }
    }

    fun signInWithGoogle(
        gsa: GoogleSignInAccount,
        navToChoosePlan: () -> Unit,
        navController: NavHostController
    ) {
        login(
            Login(
                AccessToken = gsa.idToken ?: "",
                AppDeviceId = state.value.appDeviceId,
                City = state.value.city,
                Country = state.value.country,
                CountryCode = state.value.countryCode,
                DeviceInfo = state.value.deviceInfo,
                FcmDeviceId = state.value.fcmDeviceId.ifEmpty { state.value.appDeviceId },
                Latitude = state.value.latitude,
                LoginCode = state.value.loginCode,
                Longitude = state.value.longitude,
                Password = state.value.password,
                RegisterWith = "G",
                TelcoProvider = Common.getTelcoProvider(state.value.mobile),
                UserName = gsa.id ?: "",
                UserFullName = gsa.displayName ?: "",
                ImageUrl = gsa.photoUrl.toString()
            ),
            navToChoosePlan,
            navController
        )
    }

    fun showPassword() {
        _state.value = state.value.copy(showPassword = !state.value.showPassword)
    }
}