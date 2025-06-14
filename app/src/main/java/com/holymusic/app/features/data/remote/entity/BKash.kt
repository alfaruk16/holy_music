package com.holymusic.app.features.data.remote.entity

data class BKash(
    val MSISDN: String,
    val serviceid: String,
    val puser: String = "",
    val clientCallBack: String = ""
)