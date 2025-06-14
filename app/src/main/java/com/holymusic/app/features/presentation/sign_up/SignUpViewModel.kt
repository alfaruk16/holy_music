package com.holymusic.app.features.presentation.sign_up

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.result.IntentSenderRequest
import androidx.compose.ui.focus.FocusRequester
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
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
import com.holymusic.app.core.util.LocalConstant
import com.holymusic.app.features.data.remote.entity.SignUp
import com.holymusic.app.features.domain.use_case.SignUpUseCase
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


@SuppressLint("HardwareIds")
@HiltViewModel
class SignUpViewModel @Inject constructor(
    application: Application,
    private val signUpUseCase: SignUpUseCase
) :
    AndroidViewModel(application) {

    private val _state = MutableStateFlow(SignUpState())
    val state: StateFlow<SignUpState> = _state.asStateFlow()


    private var fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)
    private var preferences: SharedPreferences = application.getSharedPreferences(
        LocalConstant.sharedPreferences,
        Context.MODE_PRIVATE
    )

    init {
        getUniqueId(application.baseContext)
        viewModelScope.launch {
            getFirebaseToken()
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

    fun mobileChanged(mobile: String) {
        _state.value = state.value.copy(mobile = mobile)
        validate()
    }

    fun passwordChanged(password: String) {
        _state.value = state.value.copy(password = password)
        validate()
    }

    fun confirmPasswordChanged(password: String) {
        _state.value = state.value.copy(confirmPassword = password)
        validate()
    }

    private fun validate() {
        if (state.value.mobile.length == 11 && state.value.password.length > 5 && state.value.confirmPassword.length > 5 && state.value.password == state.value.confirmPassword && state.value.agree) {
            _state.value = state.value.copy(valid = true)
        } else {
            _state.value = state.value.copy(valid = false)
        }
    }

    fun signUp(
        navToChoosePlan: () -> Unit,
        mobileFocusRequester: FocusRequester,
        passwordFocusRequester: FocusRequester,
        confirmPasswordFocusRequester: FocusRequester,
        navController: NavController
    ) {
        _state.value = state.value.copy(isValidate = true)

        if (!Common.isValidMobile(state.value.mobile)
        ) {
            mobileFocusRequester.requestFocus()
        } else if (state.value.password.isEmpty() || state.value.password.length < 6) {
            passwordFocusRequester.requestFocus()
        } else if (state.value.confirmPassword.isEmpty() || state.value.confirmPassword.length < 6 || state.value.password != state.value.confirmPassword) {
            confirmPasswordFocusRequester.requestFocus()
        } else if (!state.value.agree) {
            _state.value = state.value.copy(message = "Check terms and condition", showSnackBar = true)
        } else if (!state.value.isLoading) {
            signUpUseCase(
                SignUp(
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
                    RegisterWith = state.value.registerWith,
                    TelcoProvider = Common.getTelcoProvider(state.value.mobile),
                    UserName = "88" + state.value.mobile
                )
            ).onEach { result ->

                when (result) {
                    is Async.Success -> {
                        _state.value = state.value.copy(
                            showSnackBar = true,
                            message = result.data?.message ?: "Sign up success"
                        )
                        val editor: SharedPreferences.Editor = preferences.edit()
                        editor.putString(LocalConstant.token, result.data?.data?.token)
                        editor.putString(LocalConstant.mobile, "88" + state.value.mobile)
                        editor.apply()

                        MainActivity.token = "Bearer " + result.data?.data?.token
                        MainActivity.isLoggedIn = true

                        delay(1000)
                        navController.navigateUp()
                        navController.navigateUp()
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

    }


    fun closeSnackBar() {
        _state.value = state.value.copy(showSnackBar = false)
    }

    fun agreeChanged(it: Boolean) {
        _state.value = state.value.copy(agree = it)
        validate()
    }

    fun showPassword() {
        _state.value = state.value.copy(showPassword = !state.value.showPassword)
    }

    fun showConPassword(){
        _state.value = state.value.copy(showConPassword = !state.value.showConPassword)
    }

}