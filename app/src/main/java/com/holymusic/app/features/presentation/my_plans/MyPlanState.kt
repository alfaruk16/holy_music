package com.holymusic.app.features.presentation.my_plans

import com.holymusic.app.features.data.remote.model.SubStatusDto

data class MyPlanState(
    val isLoading: Boolean = true,
    val showSnackBar : Boolean = false,
    val message: String = "",
    val serviceId: String = "3001",
    val user: String = "",
    val myPlans: SubStatusDto = SubStatusDto(),
    val robiCancelCode: String = ""
)
