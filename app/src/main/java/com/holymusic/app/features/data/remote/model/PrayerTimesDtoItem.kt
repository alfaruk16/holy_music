package com.holymusic.app.features.data.remote.model

data class PrayerTimesDtoItem(
    val asr: String? = null,
    val date_month: String? = null,
    val day_no: Int? = null,
    val day_no_leap_year: Int? = null,
    val fazr: String? = null,
    val isha: String? = null,
    val ishraq: String? = null,
    val juhr: String? = null,
    val magrib: String? = null,
    val noon: String? = null,
    val sehri: String? = null,
    val sunrise: String? = null,
    val tahajjut: String? = null
)