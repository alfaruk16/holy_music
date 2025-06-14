package com.holymusic.app.features.presentation.ssl

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.holymusic.app.MainActivity
import com.holymusic.app.core.util.Async
import com.holymusic.app.core.util.Enums
import com.holymusic.app.core.util.LocalConstant
import com.holymusic.app.features.data.remote.entity.SubStatus
import com.holymusic.app.features.domain.use_case.GetSubscriptionsUseCase
import com.holymusic.app.features.presentation.ScreenArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SSLViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val getSubscriptionsUseCase: GetSubscriptionsUseCase
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(SSLState())
    val state: StateFlow<SSLState> = _state.asStateFlow()

    private val url: String = checkNotNull(savedStateHandle[ScreenArgs.TITLE] ?: "")

    private val sharedPreferences =
        application.getSharedPreferences(LocalConstant.sharedPreferences, MODE_PRIVATE)

    init {
        viewModelScope.launch {
            _state.value = state.value.copy(
                url = url,
                user = sharedPreferences.getString(LocalConstant.mobile, "") ?: ""
            )
        }
    }

    fun onPaymentSuccess(navController: NavHostController) {
        getSubscriptionsUseCase.invoke(SubStatus(MSISDN = state.value.user))
            .onEach { result ->
                when (result) {
                    is Async.Success -> {
                        if (!result.data.isNullOrEmpty()) {
                            for (item in result.data) {
                                if (item.regstatus == Enums.Subscriptions.Subscribed.name) {
                                    MainActivity.isPremium.value = true
                                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                                    editor.putBoolean(LocalConstant.isPremium, MainActivity.isPremium.value)
                                    editor.apply()
                                    navController.popBackStack()
                                    navController.popBackStack()
                                    break
                                }
                            }
                        }
                    }

                    is Async.Loading -> {}
                    is Async.Error -> {}
                }
            }.launchIn(viewModelScope)

    }
    fun onPageLoaded() {
        _state.value = state.value.copy(isLoading = false)
    }

}