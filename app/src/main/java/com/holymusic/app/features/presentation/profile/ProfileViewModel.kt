package com.holymusic.app.features.presentation.profile

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.activity.ComponentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.holymusic.app.MainActivity
import com.holymusic.app.core.util.Async
import com.holymusic.app.core.util.Enums
import com.holymusic.app.core.util.LocalConstant
import com.holymusic.app.core.util.LocalConstant.freeToken
import com.holymusic.app.features.data.remote.entity.SubStatus
import com.holymusic.app.features.data.remote.model.ProfileDto
import com.holymusic.app.features.domain.use_case.GetProfileUseCase
import com.holymusic.app.features.domain.use_case.GetSubscriptionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val getProfileUseCase: GetProfileUseCase,
    private val getSubscriptionsUseCase: GetSubscriptionsUseCase
) :
    AndroidViewModel(application) {

    private var preferences: SharedPreferences = application.getSharedPreferences(
        LocalConstant.sharedPreferences,
        Context.MODE_PRIVATE
    )

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        checkPhone()
    }

    fun checkSubscriptions() {
        getSubscriptionsUseCase.invoke(body = SubStatus(MSISDN = state.value.phone))
            .onEach { result ->
                when (result) {
                    is Async.Success -> {
                        result.data?.sortBy { it.serviceid }
                        var isPremium = false
                        for (item in result.data ?: emptyList()) {
                            if (item.regstatus == Enums.Subscriptions.Subscribed.name) {
                                _state.value = state.value.copy(plan = item.servicename ?: "")
                                isPremium = true
                                break
                            }
                        }
                        MainActivity.isPremium.value = isPremium
                    }

                    else -> {

                    }
                }
            }.launchIn(viewModelScope)
    }

    fun getProfile() {
        getProfileUseCase.invoke().onEach { result ->
            when (result) {
                is Async.Success ->
                    _state.value = state.value.copy(profile = result.data ?: ProfileDto())

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun checkPhone() {
        _state.value =
            state.value.copy(phone = preferences.getString(LocalConstant.mobile, "") ?: "")
    }

    fun logOut() {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.clear()
        editor.apply()
        MainActivity.isLoggedIn = false
        MainActivity.token = freeToken
        MainActivity.isPremium.value = false
    }
}