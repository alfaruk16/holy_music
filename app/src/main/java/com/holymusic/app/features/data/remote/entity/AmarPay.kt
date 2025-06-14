package com.holymusic.app.features.data.remote.entity

data class AmarPay(
    val MSISDN: String,
    val channel: String = "APP",
    val clientCallBack: String = "",
    val cus_email: String = "",
    val cus_name: String = "",
    val puser: String = "",
    val serviceid: String
)