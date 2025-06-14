package com.holymusic.app.features.presentation.main

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.holymusic.app.core.exoplayer.viewmodels.AudioExoPlayer
import com.holymusic.app.core.util.LocalConstant
import com.holymusic.app.features.data.local.model.Download
import com.holymusic.app.features.data.local.model.FileItem
import com.holymusic.app.features.domain.repository.DownloadRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.schedule

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val trackRepo: DownloadRepo,
    val audioExoPlayer: AudioExoPlayer
) :
    AndroidViewModel(application) {

    private val sharedPreferences =
        application.getSharedPreferences(LocalConstant.sharedPreferences, Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    init {
        disableSplash()
        checkLocal()
    }

    private fun checkLocal() {
        val file = sharedPreferences.getString(LocalConstant.local, "")
        if (!file.isNullOrEmpty()) {
            addToLocalDatabase(Gson().fromJson(file, FileItem::class.java))
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.remove(LocalConstant.local)
            editor.apply()
        }
    }


    private fun addToLocalDatabase(file: FileItem) {
        viewModelScope.launch {
            trackRepo.createTrack(
                Download(
                    id = file.id,
                    title = file.name,
                    artistName = file.artistName,
                    description = file.description,
                    duration = file.duration,
                    percentage = 100,
                    isComplete = true,
                    contentBaseUrl = file.contentBaseUrl,
                    imageUrl = file.imageUrl,
                    localPath = file.url,
                    contentType = file.mimeType
                )
            )
        }
    }


    private fun disableSplash() {
        viewModelScope.launch {
            Timer().schedule(3000) {
                _state.value = MainState(splash = false)
            }
        }
    }

    fun closeLoader() {
        _state.value = MainState(splash = false)
    }
}