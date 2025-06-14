package com.holymusic.app.features.data.remote.entity

data class Login(
    val AccessToken: String = "",
    val AppDeviceId: String = "",
    val City: String = "",
    val Country: String = "",
    val CountryCode: String = "",
    val DeviceInfo: String = "",
    val FcmDeviceId: String = "",
    val Gender: String = "",
    val ImageUrl: String = "",
    val Latitude: Double = 0.0,
    val LoginCode: String = "",
    val Longitude: Double = 0.0,
    val Password: String = "",
    val RegisterWith: String = "",
    val TelcoProvider: String = "",
    val UserName: String = "",
    val UserFullName: String = "",
    val AppTypes: String = "ht"
)