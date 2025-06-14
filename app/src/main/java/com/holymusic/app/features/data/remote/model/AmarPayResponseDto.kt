package com.holymusic.app.features.data.remote.model

data class AmarPayResponseDto(
    val errorCode: String? = null,
    val errorMessage: String? = null,
    val redirectURL: String? = null,
    val subscriptionRequestId: Any? = null,
    val timeStamp: Any? = null
)