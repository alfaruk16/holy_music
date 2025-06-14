package com.holymusic.app.features.data.remote.entity

data class SMSRegistration(
    val msisdn: String,
    val servicename: String = "",
    val user: String = ""
)