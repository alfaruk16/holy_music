package com.holymusic.app.features.data.remote.entity

data class SignUp(
    val AppDeviceId: String,
    val City: String? = null,
    val Country: String? = null,
    val CountryCode: String? = null,
    val DeviceInfo: String? = null,
    val FcmDeviceId: String? = null,
    val Latitude: Double? = null,
    val LoginCode: String? = null,
    val Longitude: Double? = null,
    val Password: String,
    val RegisterWith: String? = null,
    val TelcoProvider: String,
    val UserName: String,
    val AppTypes: String = "ht"
)