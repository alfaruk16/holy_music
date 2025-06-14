package com.holymusic.app.features.data.remote.model

data class BKashResponseDto(
    val subscriptionRequestId: String? = null,
    val redirectURL: String? = null,
    val timeStamp: Any? = null,
    val errorCode: String? = null,
    val errorMessage: Any? = null
)