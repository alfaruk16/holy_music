package com.holymusic.app.features.data.local.model

import androidx.annotation.StringRes

data class Prayer(
    val nowTime: String? = null,
    @StringRes val prayerName: Int? = null,
    val time: String? = null,
    val image: Int? = null,
    @StringRes val nextPrayerName: Int? = null,
    val nextPrayerTime: String? = null,
    val isFinished: Boolean? = null
)