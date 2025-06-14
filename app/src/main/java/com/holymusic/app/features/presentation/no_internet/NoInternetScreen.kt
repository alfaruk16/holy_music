package com.holymusic.app.features.presentation.no_internet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.holymusic.app.core.components.NoInternet
import com.holymusic.app.core.util.isOnline

@Composable
fun NoInternetScreen(
    modifier: Modifier = Modifier,
    navToHome: () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { paddingValues ->

        val context = LocalContext.current

        Box(modifier = Modifier.padding(paddingValues)) {
            NoInternet {
                if (isOnline(context)) {
                    navToHome()
                }
            }
        }
    }
}