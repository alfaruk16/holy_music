package com.holymusic.app.features.data.remote.entity

data class Otp(
    val action: String = "",
    val msisdn: String,
    val servicename: String = "",
    val user: String = ""
)