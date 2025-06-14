package com.holymusic.app.features.presentation.sign_up

data class SignUpState(
    val mobile: String = "",
    val password: String = "",
    val confirmPassword: String = "",
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
    val registerWith: String = "M",
    val telcoProvider: String = "",
    val isLoading: Boolean = false,
    val showSnackBar : Boolean = false,
    val message: String = "",
    val agree: Boolean = true,
    val showPassword: Boolean = false,
    val showConPassword: Boolean = false
)
