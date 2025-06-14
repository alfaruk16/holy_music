package com.holymusic.app.features.presentation.login

data class LoginState(
    val mobile: String = "",
    val password: String = "", val rememberMe: Boolean = false,
    val isValidate: Boolean = false,
    val valid: Boolean = false,
    val appDeviceId: String = "",
    val city: String = "Dhaka",
    val country: String = "Bangladesh",
    val countryCode: String = "BD",
    val deviceInfo: String = "",
    val fcmDeviceId: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val loginCode: String = "",
    val telcoProvider: String = "",
    val isLoading: Boolean = false,
    val showSnackBar : Boolean = false,
    val message: String = "",
    val showPassword: Boolean = false
)
