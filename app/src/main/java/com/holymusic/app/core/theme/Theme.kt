package com.holymusic.app.core.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    primaryContainer = DarkBackGroundDark,
    background = DarkBackGroundColor,
    onBackground = DarkBackGroundDark,
    secondary = DarkGray,
    onSecondary = DarkGrayLight,
    onSecondaryContainer = DarkGrayExtraLight,
    tertiary = DarkGrayLight,
    onTertiary = DarkGrayLight,
    surface = DarkTextGradientColor,
    onSurface = DarkTextGradientColor2,
    surfaceBright = DarkBorderColor,
    surfaceDim = DarkBackGroundDark,
    inverseSurface = DarkDeepGreen,
    inverseOnSurface = DarkBackGroundDark,
    error = DarkOrange,
    onError = DarkOrangeLight,
    scrim = DarkGolden
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    primaryContainer = White,
    background = BackGroundColor,
    onBackground = BackGroundDark,
    secondary = Gray,
    onSecondary = GrayLight,
    onSecondaryContainer = GrayExtraLight,
    tertiary = GradientColor1,
    onTertiary = GradientColor2,
    surface = TextGradientColor,
    onSurface = TextGradientColor2,
    surfaceBright = BorderColor,
    surfaceDim = TextFieldBackGround,
    inverseSurface = DeepGreen,
    inverseOnSurface = GreenLight,
    error = Orange,
    onError = OrangeLight,
    scrim = Golden


    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = MaterialTheme.colorScheme.primaryContainer,
    onSecondary = MaterialTheme.colorScheme.primaryContainer,
    onTertiary = MaterialTheme.colorScheme.primaryContainer,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun HolyMusicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}