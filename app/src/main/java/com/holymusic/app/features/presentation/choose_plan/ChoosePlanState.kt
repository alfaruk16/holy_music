package com.holymusic.app.features.presentation.choose_plan

import androidx.compose.ui.text.AnnotatedString
import com.holymusic.app.features.data.remote.entity.Plan
import com.holymusic.app.features.data.remote.model.TelcoResponseDto

data class ChoosePlanState(
    val isLoading: Boolean = false,
    val showSnackBar: Boolean = false,
    val message: String = "",
    val user: String = "",
    val agree: Boolean = true,
    val error: String = "Please check the privacy policy, T&C, refund policy",
    val mobile: String = "",
    val isValidate: Boolean = false,
    val banglalinkResponseDto: TelcoResponseDto = TelcoResponseDto(),
    val robiResponse: TelcoResponseDto = TelcoResponseDto(),
    val processing: Boolean = false,
    val alreadyRegister: String = "",
    val paymentSuccessStatus: Boolean = false,
    val pin: String = "",
    val failed: String = "",
    val robiPlans: List<Plan> = listOf(
        Plan(
            planName = "Robi Daily",
            planType = "Auto Renew",
            chargeAmount = "4",
            serviceId = "1045"
        ),
//        Plan(
//            planName = "Robi Monthly",
//            planType = "Auto Renew",
//            chargeAmount = "15",
//            serviceId = "1046"
//        )
    ),
    val banglalinkPlans: List<Plan> = listOf(
        Plan(
            planName = "BanglaLink Daily",
            planType = "Auto Renew",
            chargeAmount = "2",
            serviceId = "1035"
        ),
        Plan(
            planName = "BanglaLink Monthly",
            planType = "Auto Renew",
            chargeAmount = "15",
            serviceId = "1036"
        )
    ),
    val bkashPlans: List<Plan> = listOf(
        Plan(
            planName = "bKash Daily",
            planType = "Auto Renew",
            chargeAmount = "2",
            serviceId = "1025"
        ),
        Plan(
            planName = "bKash Weekly",
            planType = "Auto Renew",
            chargeAmount = "7",
            serviceId = "1026"
        ),
        Plan(
            planName = "bKash Monthly",
            planType = "Auto Renew",
            chargeAmount = "20",
            serviceId = "1027"
        ),
        Plan(
            planName = "bKash Half Yearly",
            planType = "Auto Renew",
            chargeAmount = "99",
            serviceId = "1028"
        )
    ),
    val nagadPlans: List<Plan> = listOf(
        Plan(
            planName = "Nagad Monthly",
            planType = "OnDemand",
            chargeAmount = "20",
            serviceId = "2035"
        ),
        Plan(
            planName = "Nagad Half Yearly",
            planType = "OnDemand",
            chargeAmount = "99",
            serviceId = "2036"
        ),
        Plan(
            planName = "Nagad Yearly",
            planType = "OnDemand",
            chargeAmount = "199",
            serviceId = "2037"
        )
    ),
    val sslPlans: List<Plan> = listOf(
        Plan(
            planName = "HolyTune Monthly",
            planType = "OnDemand",
            chargeAmount = "20",
            serviceId = "2010"
        ), Plan(
            planName = "HolyTune Half Yearly",
            planType = "OnDemand",
            chargeAmount = "99",
            serviceId = "2011"
        ), Plan(
            planName = "HolyTune Yearly",
            planType = "OnDemand",
            chargeAmount = "199",
            serviceId = "2012"
        )
    ),
    val amarPayPlan: List<Plan> = listOf(
        Plan(
            planName = "HolyTune Monthly",
            planType = "OnDemand",
            chargeAmount = "20",
            serviceId = "2025"
        ), Plan(
            planName = "HolyTune Half Yearly",
            planType = "OnDemand",
            chargeAmount = "99",
            serviceId = "2026"
        ), Plan(
            planName = "HolyTune Yearly",
            planType = "OnDemand",
            chargeAmount = "199",
            serviceId = "2027"
        )
    ),
    val selectedPlan: Plan = Plan(planName = "", planType = "", chargeAmount = "", serviceId = ""),
    val selectedOperator: String = ""
)

data class PaymentOptions(
    val title: AnnotatedString,
    val image: Int,
    val icon: Int,
    val plans: List<Plan>,
    val operator: String,
    val selected: (Plan) -> Unit
)


