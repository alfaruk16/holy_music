package com.holymusic.app.features.data.remote.entity

data class PinVerify(
    val msisdn: String,
    val otp: String,
    val referenceNo: String,
    val serviceid: String
)