package com.holymusic.app.features.presentation.my_plans

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.holymusic.app.MainActivity
import com.holymusic.app.core.util.Async
import com.holymusic.app.core.util.Enums
import com.holymusic.app.core.util.LocalConstant
import com.holymusic.app.features.data.remote.entity.Banglalink
import com.holymusic.app.features.data.remote.entity.BkashCancel
import com.holymusic.app.features.data.remote.entity.SSL
import com.holymusic.app.features.data.remote.entity.SubStatus
import com.holymusic.app.features.data.remote.model.SubStatusDto
import com.holymusic.app.features.data.remote.model.SubStatusDtoItem
import com.holymusic.app.features.domain.use_case.BkashCancelUseCase
import com.holymusic.app.features.domain.use_case.CancelPlanUseCase
import com.holymusic.app.features.domain.use_case.GetSubscriptionsUseCase
import com.holymusic.app.features.domain.use_case.InitiateBanglalinkPaymentUseCase
import com.holymusic.app.features.domain.use_case.InitiateRobiPaymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class MyPlanViewModel @Inject constructor(
    application: Application,
    private val getSubscriptionsUseCase: GetSubscriptionsUseCase,
    private val cancelPlanUseCase: CancelPlanUseCase,
    private val initiateBanglalinkPaymentUseCase: InitiateBanglalinkPaymentUseCase,
    private val bkashCancelUseCase: BkashCancelUseCase,
    private val initiateRobiPaymentUseCase: InitiateRobiPaymentUseCase,
) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences(
        LocalConstant.sharedPreferences,
        MODE_PRIVATE
    )

    private val _state = MutableStateFlow(MyPlanState())
    val state: StateFlow<MyPlanState> = _state.asStateFlow()

    init {
        getUserPhoneNumber()
        checkSubscriptions()
    }

    private fun checkSubscriptions(navController: NavHostController? = null) {
        getSubscriptionsUseCase.invoke(body = SubStatus(MSISDN = state.value.user))
            .onEach { result ->
                when (result) {
                    is Async.Success -> {
                        result.data?.sortBy { it.serviceid }

                        _state.value = state.value.copy(
                            isLoading = false,
                            myPlans = result.data ?: SubStatusDto()
                        )

                        var isPremium = false
                        if (!result.data.isNullOrEmpty()) {
                            for (item in result.data) {
                                if (item.regstatus == Enums.Subscriptions.Subscribed.name) {
                                    MainActivity.isPremium.value = true
                                    isPremium = true
                                    break
                                }
                            }
                        }

                        if (!isPremium) {
                            MainActivity.isPremium.value = false
                            val editor: SharedPreferences.Editor = sharedPreferences.edit()
                            editor.putBoolean(LocalConstant.isPremium, MainActivity.isPremium.value)
                            editor.apply()
                            navController?.navigateUp()
                        }
                    }

                    is Async.Loading -> {
                        _state.value = state.value.copy(isLoading = true)
                    }

                    else -> {
                        _state.value = state.value.copy(isLoading = false)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun getUserPhoneNumber() {
        _state.value =
            state.value.copy(user = sharedPreferences.getString(LocalConstant.mobile, "") ?: "")
    }

    fun closeSnackBar() {
        _state.value = state.value.copy(showSnackBar = false)
    }

    fun cancelPlan(navController: NavHostController? = null, item: SubStatusDtoItem) {
        if (!state.value.isLoading) {
            when (item.serviceid) {

                "1045", "1046" -> {
                    _state.value =
                        state.value.copy(robiCancelCode = if (item.serviceid == "1045") "641" else "hta2")
                }

                "1035", "1036" -> {
                    cancelBanglalinkPlan(item, navController)
                }

                "1025", "1026", "1027", "1028" -> {
                    cancelBkashPlan(item, navController)
                }

                else -> {
                    cancelPlanUseCase.invoke(
                        SSL(
                            MSISDN = state.value.user,
                            serviceid = item.serviceid ?: ""
                        )
                    ).onEach { result ->
                        when (result) {
                            is Async.Success -> {
                                _state.value = state.value.copy(isLoading = false)
                                checkSubscriptions(navController)
                            }

                            is Async.Loading -> {
                                _state.value = state.value.copy(isLoading = true)
                            }

                            is Async.Error -> {
                                _state.value = state.value.copy(isLoading = false)
                            }
                        }
                    }.launchIn(viewModelScope)
                }
            }
        }
    }

    private fun cancelBkashPlan(item: SubStatusDtoItem, navController: NavHostController?) {
        bkashCancelUseCase.invoke(
            BkashCancel(
                MSISDN = state.value.user,
                serviceid = item.serviceid ?: ""
            )
        ).onEach { result ->
            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(isLoading = false)
                    checkSubscriptions(navController)
                }

                is Async.Loading -> _state.value = state.value.copy(isLoading = true)
                is Async.Error -> _state.value = state.value.copy(isLoading = false)
            }

        }.launchIn(viewModelScope)
    }

    private fun cancelBanglalinkPlan(item: SubStatusDtoItem, navController: NavHostController?) {

        initiateBanglalinkPaymentUseCase.invoke(
            Banglalink(
                msisdn = state.value.user,
                serviceid = item.serviceid ?: "",
                action = "0"
            )
        ).onEach { result ->
            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(isLoading = false)
                    checkSubscriptions(navController)
                }

                is Async.Loading -> _state.value = state.value.copy(isLoading = true)
                is Async.Error -> _state.value = state.value.copy(isLoading = false)
            }

        }.launchIn(viewModelScope)
    }

    fun closeRobiCancelDialogue() {
        _state.value = state.value.copy(robiCancelCode = "")
    }

}