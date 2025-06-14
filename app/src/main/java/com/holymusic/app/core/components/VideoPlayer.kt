package com.holymusic.app.core.components

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.PauseCircleOutline
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.holymusic.app.R
import com.holymusic.app.core.theme.Orange
import com.holymusic.app.core.theme.Primary
import com.holymusic.app.core.theme.Typography
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.coroutines.delay

@SuppressLint("UnrememberedMutableInteractionSource")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayer(
    url: String, addPlayCount: (Long) -> Unit,
    navController: NavController,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    autoPlay: Boolean = true
) {

    val context = LocalContext.current
    val activity = context as Activity
    val systemUiController: SystemUiController = rememberSystemUiController()
    val window = activity.window
    val width = LocalConfiguration.current.screenWidthDp
    val height = LocalConfiguration.current.screenHeightDp
    val density = LocalDensity.current.density

    val isFullScreen = remember {
        mutableStateOf(false)
    }
    val stream = remember {
        mutableIntStateOf(0)
    }
    val currentDuration = remember {
        mutableFloatStateOf(0f)
    }
    val currentBuffering = remember {
        mutableFloatStateOf(0f)
    }

    val isPlaying = remember {
        mutableStateOf(autoPlay)
    }

    val userControlHideIn = remember {
        mutableIntStateOf(if (autoPlay) 0 else 3)
    }

    val backWardIn = remember {
        mutableIntStateOf(0)
    }

    val forwardIn = remember {
        mutableIntStateOf(0)
    }

    val currentTime = remember {
        mutableStateOf("")
    }

    val duration = remember {
        mutableStateOf("")
    }

    val totalDuration = remember {
        mutableFloatStateOf(0f)
    }

    val replay = remember {
        mutableStateOf(false)
    }

    val loading = remember {
        mutableStateOf(true)
    }

    val exoPlayer = remember {
        com.google.android.exoplayer2.ExoPlayer.Builder(context).build().apply {
            addMediaItem(MediaItem.fromUri(url))
            prepare()
            playWhenReady = autoPlay
        }
    }

    exoPlayer.addListener(object : Player.Listener {
        @Deprecated("Deprecated in Java")
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

            when (playbackState) {
                Player.STATE_IDLE -> {}
                Player.STATE_BUFFERING -> {}
                Player.STATE_READY -> {
                    if (loading.value) {
                        loading.value = false
                    }
                }

                Player.STATE_ENDED -> {
                    replay.value = true
                    userControlHideIn.intValue = 3
                    if (isFullScreen.value) {
                        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                        systemUiController.isStatusBarVisible = true
                        isFullScreen.value = false
                    }
                }
            }
        }
    })

    val playerView = remember {
        StyledPlayerView(context, null, 0).apply {
            player = exoPlayer
            setFullscreenButtonClickListener { isFullscreen ->
                isFullScreen.value = isFullscreen
                if (isFullscreen) {
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    systemUiController.isStatusBarVisible = false
                } else {
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    systemUiController.isStatusBarVisible = true
                }
            }
            showController()

            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            if (exoPlayer.isPlaying) {
                stream.intValue += 1
                window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
            if (exoPlayer.isPlaying) {
                currentDuration.floatValue = exoPlayer.currentPosition.toFloat()
                currentBuffering.floatValue = exoPlayer.bufferedPercentage.toFloat() / 100

                val min = (currentDuration.floatValue / 1000).toInt() / 60
                val sec = (currentDuration.floatValue / 1000).toInt() % 60
                currentTime.value =
                    (if (min < 10) "0" else "") + min.toString() + ":" + (if (sec < 10) "0" else "") + sec.toString()
                if (duration.value.isEmpty()) {
                    totalDuration.floatValue = exoPlayer.duration.toFloat()
                    val mind = (totalDuration.floatValue / 1000).toInt() / 60
                    val secd = (totalDuration.floatValue / 1000).toInt() % 60
                    duration.value =
                        (if (mind < 10) "0" else "") + mind.toString() + ":" + (if (secd < 10) "0" else "") + secd.toString()
                }
                if (userControlHideIn.intValue > 0) {
                    userControlHideIn.intValue = userControlHideIn.intValue - 1
                }
            }
            if (backWardIn.intValue > 0) {
                backWardIn.intValue = backWardIn.intValue - 1
            }
            if (forwardIn.intValue > 0) {
                forwardIn.intValue = forwardIn.intValue - 1
            }
            delay(1000)
        }
    }

    BackHandler {
        if (isFullScreen.value) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            systemUiController.isStatusBarVisible = true
            isFullScreen.value = false
        } else {
            navController.popBackStack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            if (userControlHideIn.intValue > 0) {
                                userControlHideIn.intValue = 0
                            } else {
                                userControlHideIn.intValue = 3
                            }
                            if (isFullScreen.value) {
                                systemUiController.isStatusBarVisible = false
                            }
                        },
                        onDoubleTap = {
                            val left =
                                it.x / density < (if (isFullScreen.value) height else width) / 2
                            if (left) {
                                exoPlayer.seekTo(exoPlayer.currentPosition - 10000)
                                backWardIn.intValue = 2
                            } else {
                                exoPlayer.seekTo(exoPlayer.currentPosition + 10000)
                                forwardIn.intValue = 2
                            }
                            currentDuration.floatValue = exoPlayer.currentPosition.toFloat()
                            val min = (currentDuration.floatValue / 1000).toInt() / 60
                            val sec = (currentDuration.floatValue / 1000).toInt() % 60
                            currentTime.value =
                                (if (min < 10) "0" else "") + min.toString() + ":" + (if (sec < 10) "0" else "") + sec.toString()
                        }
                    )
                },
            factory = {
                playerView
            }
        )

        if (loading.value) Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = MaterialTheme.colorScheme.error,
                strokeWidth = 4.dp
            )
        }

        if (forwardIn.intValue > 0 || backWardIn.intValue > 0) {

            if (backWardIn.intValue > 0) {
                Column(
                    modifier = Modifier
                        .padding(start = 50.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardDoubleArrowLeft,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(text = stringResource(id = R.string.ten_second), color = MaterialTheme.colorScheme.primaryContainer,
                        style = Typography.displaySmall)
                }
            }
            if (forwardIn.intValue > 0) {
                Column(
                    modifier = Modifier
                        .padding(end = 50.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardDoubleArrowRight,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(text = stringResource(id = R.string.ten_second), color = MaterialTheme.colorScheme.primaryContainer,
                        style = Typography.displaySmall)
                }
            }
        } else if (userControlHideIn.intValue > 0) {
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.height(12.dp))
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .clip(shape = CircleShape).clickable {
                            if (isFullScreen.value) {
                                activity.requestedOrientation =
                                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                                systemUiController.isStatusBarVisible = true
                                isFullScreen.value = false
                            } else {
                                navController.popBackStack()
                            }
                        }
                )
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (replay.value) Icon(Icons.Filled.Replay,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(shape = CircleShape).clickable {
                                replay.value = false
                                userControlHideIn.intValue = 1
                                currentDuration.floatValue = 0f
                                currentTime.value = "00:00"
                                exoPlayer.seekTo(0)
                            }
                    )
                    else if (isPlaying.value) Icon(Icons.Filled.PauseCircleOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(shape = CircleShape).clickable {
                                if (exoPlayer.isPlaying) {
                                    exoPlayer.pause()
                                    isPlaying.value = false
                                }
                            }
                    )
                    else Icon(
                        imageVector = Icons.Filled.PlayCircleOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(shape = CircleShape).clickable {
                                exoPlayer.play()
                                isPlaying.value = true
                            }
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = if (currentTime.value.isNotEmpty()) (currentTime.value + " / " + duration.value) else "",
                        color = MaterialTheme.colorScheme.primaryContainer,
                        fontSize = 12.sp,
                        style = Typography.displaySmall
                    )
                    if (!isFullScreen.value)
                        Icon(
                            imageVector = Icons.Filled.Fullscreen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.clip(shape = CircleShape).clickable {
                                activity.requestedOrientation =
                                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                                systemUiController.isStatusBarVisible = false
                                isFullScreen.value = true
                            }
                        ) else Icon(
                        imageVector = Icons.Filled.FullscreenExit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.clip(shape = CircleShape).clickable {
                            activity.requestedOrientation =
                                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                            systemUiController.isStatusBarVisible = true
                            isFullScreen.value = false
                        }
                    )

                }
                if (currentDuration.floatValue != 0f && totalDuration.floatValue != 0f) {
                    Box {
                        Slider(
                            value = (currentBuffering.floatValue),
                            onValueChange = {},
                            colors = SliderDefaults.colors(
                                activeTrackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = .25f),
                                inactiveTrackColor = Color.Transparent,
                                thumbColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .height(12.dp),
                            thumb = {}
                        )

                        Slider(
                            value = (currentDuration.floatValue.div(totalDuration.floatValue)),
                            onValueChange = {
                                if (replay.value) {
                                    replay.value = false
                                }
                                userControlHideIn.intValue = 1
                                exoPlayer.seekTo((it * totalDuration.floatValue).toLong())
                                currentDuration.floatValue = it * totalDuration.floatValue
                                val min = (currentDuration.floatValue / 1000).toInt() / 60
                                val sec = (currentDuration.floatValue / 1000).toInt() % 60
                                currentTime.value =
                                    (if (min < 10) "0" else "") + min.toString() + ":" + (if (sec < 10) "0" else "") + sec.toString()
                            },
                            colors = SliderDefaults.colors(
                                activeTrackColor = Primary,
                                disabledThumbColor = Primary,
                                inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = .25f)
                            ),
                            modifier = Modifier
                                .height(12.dp),
                            thumb = {
                                SliderDefaults.Thumb(
                                    interactionSource = MutableInteractionSource(),
                                    thumbSize = DpSize(10.dp, 10.dp),
                                    modifier = Modifier.padding(
                                        top = 5.dp,
                                        start = 5.dp
                                    ),
                                    colors = SliderDefaults.colors(
                                        thumbColor = MaterialTheme.colorScheme.error
                                    )
                                )
                            },
                        )
                    }
                } else Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                exoPlayer.pause()
                isPlaying.value = false
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            if (stream.intValue >= 15) {
                addPlayCount(exoPlayer.currentPosition)
            }
            exoPlayer.release()
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}