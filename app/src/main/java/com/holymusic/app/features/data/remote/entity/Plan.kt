package com.holymusic.app.features.data.remote.entity

data class Plan(
    val planName: String,
    val planType: String,
    val chargeAmount: String,
    val serviceId: String
)

enum class Operators{
    Banglalink, BKash, SSL, AmarPay, Robi, Nagad
}