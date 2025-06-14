package com.holymusic.app.features.presentation.update_profile

data class UpdateProfileState(
    val fullName: String = "",
    val gender: String = "",
    val dateOfBirth: String = "",
    val mobile: String = "",
    val imageUrl: String = "",
    val isValidate: Boolean = false
)
