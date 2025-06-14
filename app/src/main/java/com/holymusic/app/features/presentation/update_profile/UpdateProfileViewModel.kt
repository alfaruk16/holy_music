package com.holymusic.app.features.presentation.update_profile

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.holymusic.app.core.util.Async
import com.holymusic.app.features.data.remote.entity.UpdateProfile
import com.holymusic.app.features.data.remote.model.UserProfile
import com.holymusic.app.features.domain.use_case.UpdateProfileUseCase
import com.holymusic.app.features.presentation.ScreenArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class UpdateProfileViewModel @Inject constructor(
    private val updateProfileUseCase: UpdateProfileUseCase, application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(UpdateProfileState())
    val state: StateFlow<UpdateProfileState> = _state.asStateFlow()


    private val contTentString: String = checkNotNull(savedStateHandle[ScreenArgs.CONTENT] ?: "")
    private val user = Gson().fromJson(contTentString, UserProfile::class.java)

    init {
        _state.value =
            state.value.copy(
                fullName = user.userFullName ?: "",
                mobile = user.mobileNo ?: "",
                gender = user.gender ?: "",
                dateOfBirth = if (!user.birthDate.isNullOrEmpty()) LocalDate.parse(
                    user.birthDate.replace("T", " "),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                ).format(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy")
                ) else "",
                imageUrl = user.imageUrl ?: ""
            )
    }

    fun fullNameChanged(name: String) {
        _state.value = state.value.copy(fullName = name)
    }

    fun genderChanged(gender: String) {
        _state.value = state.value.copy(gender = gender)
    }

    @SuppressLint("SimpleDateFormat")
    fun dateOfBirthChanged(timeStamp: Long) {
        val date = Date(timeStamp)
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        _state.value = state.value.copy(dateOfBirth = sdf.format(date))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun done(
        fullNameFocusRequester: FocusRequester,
        genderFocusRequester: FocusRequester,
        dateOfBirthFocusRequester: FocusRequester,
        navController: NavHostController
    ) {
        _state.value = state.value.copy(isValidate = true)
        if (state.value.fullName.isEmpty()) {
            fullNameFocusRequester.requestFocus()
        } else if (state.value.gender.isEmpty()) {
            genderFocusRequester.requestFocus()
        } else if (state.value.dateOfBirth.isEmpty()) {
            dateOfBirthFocusRequester.requestFocus()
        } else {

            val requestBody: RequestBody = Gson().toJson(

                UpdateProfile(
                    userFullName = state.value.fullName,
                    birthDate = LocalDate.parse(
                        state.value.dateOfBirth,
                        DateTimeFormatter.ofPattern("dd-MM-yyyy")
                    ).format(
                        DateTimeFormatter.ofPattern("MM-dd-yyyy")
                    ),
                    gender = state.value.gender,
                    mobileNo = state.value.mobile
                )
            )
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())

            updateProfileUseCase.invoke(
                body = requestBody
            ).onEach { result ->

                when (result) {
                    is Async.Success -> {
                        navController.popBackStack()
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        }
    }
}