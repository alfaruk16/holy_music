package com.holymusic.app.features.data.remote.entity

data class SSL(
    val MSISDN: String,
    val channel: String = "APP",
    val cus_email: String = "",
    val cus_name: String = "",
    val puser: String = "",
    val serviceid: String
)