package com.holymusic.app.features.presentation.profile

import com.holymusic.app.features.data.remote.model.ProfileDto

data class ProfileState(
    val phone: String = "",
    val profile: ProfileDto = ProfileDto(), val plan: String = ""
)
