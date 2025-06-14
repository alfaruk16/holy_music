package com.holymusic.app.features.presentation.reset_password

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.holymusic.app.core.util.Async
import com.holymusic.app.features.domain.use_case.ResetPasswordUseCase
import com.holymusic.app.features.presentation.ScreenArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    application: Application,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(ResetPasswordState())
    val state: StateFlow<ResetPasswordState> = _state.asStateFlow()

    private val mobile: String = checkNotNull(savedStateHandle[ScreenArgs.TITLE])

    init {
        _state.value = state.value.copy(mobile = mobile)
    }

    fun passwordChanged(password: String) {
        _state.value = state.value.copy(password = password)
        validate()
    }

    fun confirmPasswordChanged(password: String) {
        _state.value = state.value.copy(confirmPassword = password)
        validate()
    }

    private fun validate() {
        if (state.value.password.length > 5 && state.value.confirmPassword.length > 5 && state.value.password == state.value.confirmPassword) {
            _state.value = state.value.copy(valid = true)
        } else {
            _state.value = state.value.copy(valid = false)
        }
    }

    fun submit() {
        _state.value = state.value.copy(isValidate = true)
        if (state.value.password.length == 6) {
            resetPasswordUseCase.invoke(
                userName = "88" + state.value.mobile,
                password = state.value.password
            ).onEach { result ->
                when (result) {
                    is Async.Success -> {
                        _state.value = state.value.copy(
                            isLoading = false,
                            showDialogue = true,
                            message = "Success"
                        )
                        //navToLogin()
                    }

                    is Async.Error -> {
                        _state.value = state.value.copy(
                            isLoading = false,
                            showError = true,
                            message = result.message ?: "Unknown error"
                        )
                    }

                    is Async.Loading -> {
                        _state.value = state.value.copy(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
        }

    }

    fun closeSnackBar() {
        _state.value = state.value.copy(showError = false)
    }
}