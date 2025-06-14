package com.holymusic.app.features.presentation.choose_plan

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.holymusic.app.core.util.Async
import com.holymusic.app.core.util.Common
import com.holymusic.app.core.util.Enums
import com.holymusic.app.core.util.LocalConstant
import com.holymusic.app.features.data.remote.entity.AmarPay
import com.holymusic.app.features.data.remote.entity.BKash
import com.holymusic.app.features.data.remote.entity.Banglalink
import com.holymusic.app.features.data.remote.entity.BkashToken
import com.holymusic.app.features.data.remote.entity.Nagad
import com.holymusic.app.features.data.remote.entity.Plan
import com.holymusic.app.features.data.remote.entity.Robi
import com.holymusic.app.features.data.remote.entity.PinVerify
import com.holymusic.app.features.data.remote.entity.SMSRegistration
import com.holymusic.app.features.data.remote.entity.SSL
import com.holymusic.app.features.data.remote.entity.SubStatus
import com.holymusic.app.features.data.remote.model.TelcoResponseDto
import com.holymusic.app.features.domain.use_case.BanglalinkPinVerifyUseCase
import com.holymusic.app.features.domain.use_case.GetBkashTokenUseCase
import com.holymusic.app.features.domain.use_case.GetSubscriptionsUseCase
import com.holymusic.app.features.domain.use_case.InitialAmarPayUseCase
import com.holymusic.app.features.domain.use_case.InitialBKashPaymentUseCase
import com.holymusic.app.features.domain.use_case.InitiateBanglalinkPaymentUseCase
import com.holymusic.app.features.domain.use_case.InitiateNagadPayUseCase
import com.holymusic.app.features.domain.use_case.InitiateRobiPaymentUseCase
import com.holymusic.app.features.domain.use_case.InitiateSSLPayUseCase
import com.holymusic.app.features.domain.use_case.RobiPinVerifyUseCase
import com.holymusic.app.features.domain.use_case.SMSRegistrationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

const val alreadySubscribe = "ধন্যবাদ \nএই নম্বর টি প্রিমিয়াম প্যাকেজ এ নিবন্ধিত রয়েছে। রেজিস্ট্রেশন / লগ ইন করুন"

@HiltViewModel
class ChoosePlanViewModel @Inject constructor(
    private val initiateSSLPay: InitiateSSLPayUseCase,
    private val initialAmarPayUseCase: InitialAmarPayUseCase,
    private val initiateBanglalinkPaymentUseCase: InitiateBanglalinkPaymentUseCase,
    private val smsRegistrationUseCase: SMSRegistrationUseCase,
    private val getSubscriptionsUseCase: GetSubscriptionsUseCase,
    private val getBkashTokenUseCase: GetBkashTokenUseCase,
    private val initialBKashPaymentUseCase: InitialBKashPaymentUseCase,
    private val initiateRobiPaymentUseCase: InitiateRobiPaymentUseCase,
    private val robiPinVerifyUseCase: RobiPinVerifyUseCase,
    private val banglalinkPinVerifyUseCase: BanglalinkPinVerifyUseCase,
    application: Application,
    private val initiateNagadPayUseCase: InitiateNagadPayUseCase
) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences(
        LocalConstant.sharedPreferences,
        MODE_PRIVATE
    )

    private val _state = MutableStateFlow(ChoosePlanState())
    val state: StateFlow<ChoosePlanState> = _state.asStateFlow()

    init {
        getUserPhoneNumber()
    }

    private fun getUserPhoneNumber() {
        _state.value =
            state.value.copy(user = sharedPreferences.getString(LocalConstant.mobile, "") ?: "")
    }

    fun initiateSSLPayment(navToSSL: (String) -> Unit) {
        if (state.value.agree) {
            if (!state.value.isLoading) {
                initiateSSLPay.invoke(
                    SSL(
                        MSISDN = state.value.user,
                        serviceid = state.value.selectedPlan.serviceId
                    )
                ).onEach { result ->
                    when (result) {
                        is Async.Success -> {
                            _state.value = state.value.copy(isLoading = false)
                            navToSSL(result.data?.GatewayPageURL ?: "")
                        }

                        is Async.Loading -> _state.value = state.value.copy(isLoading = true)
                        is Async.Error -> _state.value = state.value.copy(isLoading = false)
                    }

                }.launchIn(viewModelScope)
            }
        } else {
            _state.value = state.value.copy(showSnackBar = true)
        }
    }

    fun initiateAmarPayPayment(navToSSL: (String) -> Unit) {
        if (state.value.agree) {
            if (!state.value.isLoading) {
                initialAmarPayUseCase.invoke(
                    AmarPay(
                        MSISDN = state.value.user,
                        serviceid = state.value.selectedPlan.serviceId
                    )
                ).onEach { result ->
                    when (result) {
                        is Async.Success -> {
                            _state.value = state.value.copy(isLoading = false)
                            navToSSL(result.data?.redirectURL ?: "")
                        }

                        is Async.Loading -> _state.value = state.value.copy(isLoading = true)
                        is Async.Error -> _state.value = state.value.copy(isLoading = false)
                    }

                }.launchIn(viewModelScope)
            }
        } else {
            _state.value =
                state.value.copy(showSnackBar = true)
        }
    }

    fun getBkashToken(navToSSL: (String) -> Unit) {
        if (state.value.agree) {
            if (!state.value.isLoading) {
                getBkashTokenUseCase.invoke(body = BkashToken()).onEach { result ->
                    println(result)
                    when (result) {
                        is Async.Success -> {
                            initiateBKashPayment(navToSSL, result.data?.access_token)
                        }

                        is Async.Loading -> _state.value = state.value.copy(isLoading = true)
                        is Async.Error -> _state.value = state.value.copy(isLoading = false)
                    }

                }.launchIn(viewModelScope)
            }
        } else {
            _state.value =
                state.value.copy(showSnackBar = true)
        }
    }

    private fun initiateBKashPayment(navToSSL: (String) -> Unit, accessToken: String?) {

        initialBKashPaymentUseCase.invoke(
            BKash(
                MSISDN = state.value.user,
                serviceid = state.value.selectedPlan.serviceId
            ),
            "bearer $accessToken"
        ).onEach { result ->
            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(isLoading = false)
                    navToSSL(result.data?.redirectURL ?: "")
                }

                is Async.Loading -> _state.value = state.value.copy(isLoading = true)
                is Async.Error -> _state.value = state.value.copy(isLoading = false)
            }

        }.launchIn(viewModelScope)
    }

    fun closeSnackBar() {
        _state.value = state.value.copy(showSnackBar = false)
    }

    fun planSelected(plan: Plan) {
        _state.value = state.value.copy(selectedPlan = plan)
    }

    fun agreeChanged(it: Boolean) {
        _state.value = state.value.copy(agree = it)
    }

    fun isBanglalink(mobile: String): Boolean {
        return (mobile.startsWith("88019") || mobile.startsWith("88014")) && mobile.length == 13
    }

    fun isRobi(mobile: String): Boolean {
        return (mobile.startsWith("88018") || mobile.startsWith("88016")) && mobile.length == 13
    }
    fun mobileNumberChanged(mobile: String) {
        _state.value = state.value.copy(mobile = mobile)
    }

    fun validated() {
        _state.value = state.value.copy(isValidate = true)
    }

    fun initiateBanglalinkPayment(mobile: String) {
        if (state.value.agree) {
            if (!state.value.isLoading) {
                initiateBanglalinkPaymentUseCase.invoke(
                    Banglalink(
                        msisdn = mobile,
                        serviceid = state.value.selectedPlan.serviceId,
                        action = "1"
                    )
                ).onEach { result ->
                    when (result) {
                        is Async.Success -> {
                            _state.value = state.value.copy(
                                isLoading = false
                            )
                            if (Common.isNumeric(result.data?.response ?: "")) {
                                _state.value = state.value.copy(
                                    banglalinkResponseDto = result.data ?: TelcoResponseDto()
                                )
                            } else {
                                _state.value = state.value.copy(processing = true)
                            }
                        }

                        is Async.Loading -> _state.value = state.value.copy(isLoading = true)
                        is Async.Error -> _state.value = state.value.copy(isLoading = false)
                    }

                }.launchIn(viewModelScope)
            }
        } else {
            _state.value =
                state.value.copy(showSnackBar = true)
        }
    }

    fun initiateRobiPayment(mobile: String) {
        if (state.value.agree) {
            if (!state.value.isLoading) {
                if (isRobi(state.value.user)) {
                    robiPayment(mobile)
                } else {
                    checkSubscriptionForRobi(mobile)
                }
            }
        } else {
            _state.value =
                state.value.copy(showSnackBar = true)
        }
    }

    private fun robiPayment(mobile: String) {
        initiateRobiPaymentUseCase.invoke(
            Robi(
                msisdn = mobile,
                serviceid = state.value.selectedPlan.serviceId,
                action = "1",
                device = Build.MODEL,
                os = Build.DEVICE
            )
        ).onEach { result ->
            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(
                        isLoading = false
                    )
                    if (Common.isNumeric(result.data?.response ?: "")) {
                        _state.value = state.value.copy(
                            robiResponse = result.data ?: TelcoResponseDto()
                        )
                    } else {
                        _state.value = state.value.copy(processing = true)
                    }
                }

                is Async.Loading -> _state.value = state.value.copy(isLoading = true)
                is Async.Error -> _state.value = state.value.copy(isLoading = false)
            }

        }.launchIn(viewModelScope)
    }

    private fun checkSubscriptionForRobi(mobile: String) {
        getSubscriptionsUseCase.invoke(
            body = SubStatus(
                MSISDN = (mobile)
            )
        ).onEach { result ->

            when (result) {
                is Async.Success ->
                    if (!result.data.isNullOrEmpty()) {
                        var isSubscribed = false
                        for (item in result.data) {
                            if (item.regstatus == Enums.Subscriptions.Subscribed.name) {
                                isSubscribed = true
                            }
                        }
                        if (isSubscribed) {
                            _state.value = state.value.copy(
                                alreadyRegister = alreadySubscribe
                            )
                        } else {
                            robiPayment(mobile)
                        }
                    }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun checkSubscriptionStatus(mobile: String, data: TelcoResponseDto?) {
        getSubscriptionsUseCase.invoke(
            body = SubStatus(
                MSISDN = (mobile)
            )
        ).onEach { result ->

            when (result) {
                is Async.Success ->
                    if (!result.data.isNullOrEmpty()) {
                        var isSubscribed = false
                        for (item in result.data) {
                            if (item.regstatus == Enums.Subscriptions.Subscribed.name) {
                                isSubscribed = true
                            }
                        }
                        if (isSubscribed) {
                            _state.value = state.value.copy(
                                alreadyRegister = alreadySubscribe
                            )
                        } else {
                            _state.value = state.value.copy(
                                banglalinkResponseDto = data ?: TelcoResponseDto()
                            )
                            requestSMS(mobile)
                        }
                    }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }


    private fun requestSMS(mobile: String) {
        smsRegistrationUseCase.invoke(SMSRegistration(msisdn = mobile)).onEach { result ->
            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(isLoading = false)
                }

                is Async.Loading ->
                    _state.value = state.value.copy(isLoading = true)

                is Async.Error ->
                    _state.value = state.value.copy(
                        isLoading = false,
                        showSnackBar = true,
                        message = result.message ?: "Unknown error"
                    )

            }
        }.launchIn(viewModelScope)
    }

    fun selectPaymentMethod(operator: String) {
        _state.value = state.value.copy(selectedOperator = if(operator != state.value.selectedOperator) operator else "")
        clearMobile()
    }

    fun clearMobile(){
        _state.value = state.value.copy(mobile = "", isValidate = false)
    }

    fun removeAlreadyRegister() {
        _state.value = state.value.copy(alreadyRegister = "")
    }

    fun removeRobiPaymentStatus() {
        _state.value = state.value.copy(paymentSuccessStatus = false)
    }

    fun removeProcessingDialogue() {
        _state.value = state.value.copy(processing = false)
    }

    fun closeFailedSnackBar() {
        _state.value = state.value.copy(failed = "")
    }

    fun dismissRobi() {
        _state.value = state.value.copy(robiResponse = TelcoResponseDto())
    }

    fun verifyRobiOtp(pin: String) {
        robiPinVerifyUseCase.invoke(
            PinVerify(
                msisdn = if (isRobi(state.value.user)) state.value.user else "88${state.value.mobile}",
                serviceid = state.value.selectedPlan.serviceId,
                otp = pin,
                referenceNo = state.value.robiResponse.response ?: ""
            )
        ).onEach { result ->
            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(
                        isLoading = false
                    )
                    if (!result.data?.response.isNullOrEmpty()) {
                        dismissRobi()
                        _state.value =
                            state.value.copy(paymentSuccessStatus = true)
                        if(!isRobi(state.value.user)){
                            requestSMS("88${state.value.mobile}")
                        }
                    } else {
                        dismissRobi()
                        _state.value = state.value.copy(failed = "Failed")
                    }
                }

                is Async.Loading -> _state.value = state.value.copy(isLoading = true)
                is Async.Error -> {
                    _state.value = state.value.copy(isLoading = false, failed = result.message ?: "Unknown Error")
                }
            }

        }.launchIn(viewModelScope)
    }

    fun verifyBanglalinkOtp(pin: String) {
        banglalinkPinVerifyUseCase.invoke(
            PinVerify(
                msisdn = if (isBanglalink(state.value.user)) state.value.user else "88${state.value.mobile}",
                serviceid = state.value.selectedPlan.serviceId,
                otp = pin,
                referenceNo = state.value.banglalinkResponseDto.response ?: ""
            )
        ).onEach { result ->
            when (result) {
                is Async.Success -> {
                    _state.value = state.value.copy(
                        isLoading = false
                    )
                    if (!result.data?.response.isNullOrEmpty()) {
                        dismissBanglalink()
                        _state.value =
                            state.value.copy(paymentSuccessStatus = true)
                        if (!isBanglalink(state.value.user)) {
                            requestSMS("88${state.value.mobile}")
                        }
                    } else {
                        dismissBanglalink()
                        _state.value = state.value.copy(failed = "Failed")
                    }
                }

                is Async.Loading -> _state.value = state.value.copy(isLoading = true)
                is Async.Error -> {
                    _state.value = state.value.copy(
                        isLoading = false,
                        failed = result.message ?: "Unknown Error"
                    )
                }
            }

        }.launchIn(viewModelScope)
    }

    fun pinNumberChanged(pin: String) {
        _state.value = state.value.copy(pin = pin)
    }

    fun dismissBanglalink() {
        _state.value = state.value.copy(banglalinkResponseDto = TelcoResponseDto())
    }

    fun initiateNagadPay(navToSSL: (String) -> Unit) {
        if (state.value.agree) {
            if (!state.value.isLoading) {
                initiateNagadPayUseCase.invoke(
                    Nagad(
                        MSISDN = state.value.user,
                        serviceid = state.value.selectedPlan.serviceId
                    )
                ).onEach { result ->
                    println(result.data)
                    when (result) {
                        is Async.Success -> {
                            _state.value = state.value.copy(isLoading = false)
                            navToSSL(result.data?.paymentUrl ?: "")
                        }

                        is Async.Loading -> _state.value = state.value.copy(isLoading = true)
                        is Async.Error -> _state.value = state.value.copy(isLoading = false)
                    }

                }.launchIn(viewModelScope)
            }
        } else {
            _state.value =
                state.value.copy(showSnackBar = true)
        }
    }
}