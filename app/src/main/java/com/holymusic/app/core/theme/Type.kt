package com.holymusic.app.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.holymusic.app.R

// Set of Material typography styles to start with
val Typography = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily(Font(resId = R.font.century_gothic)),
        fontWeight = FontWeight.W700
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily(Font(resId = R.font.century_gothic)),
        fontWeight = FontWeight.W700,
        fontSize = 16.sp
    ),
    displayLarge = TextStyle(
        fontFamily = FontFamily(Font(resId = R.font.century_gothic)),
        fontWeight = FontWeight.W700,
        fontSize = 18.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily(Font(resId = R.font.century_gothic)),
        fontWeight = FontWeight.W800
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily(Font(resId = R.font.century_gothic)),
        fontWeight = FontWeight.W800,
        fontSize = 16.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily(Font(resId = R.font.century_gothic)),
        fontWeight = FontWeight.W800,
        fontSize = 18.sp
    )
)