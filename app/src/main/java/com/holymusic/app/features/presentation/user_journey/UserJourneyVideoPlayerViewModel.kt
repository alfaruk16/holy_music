package com.holymusic.app.features.presentation.user_journey

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class UserJourneyVideoPlayerViewModel @Inject constructor() :
    ViewModel() {

    private val _state = MutableStateFlow(UserJourneyVideoPlayerState())
    val state: StateFlow<UserJourneyVideoPlayerState> = _state.asStateFlow()

}