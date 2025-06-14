package com.holymusic.app.features.data.remote.entity

data class UpdateProfile(
    val userFullName: String,
    val mobileNo: String,
    val imageUrl: String = "0",
    val birthDate:String,
    val gender:String
)
