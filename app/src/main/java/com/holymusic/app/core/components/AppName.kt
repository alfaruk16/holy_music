package com.holymusic.app.core.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.holymusic.app.R
import com.holymusic.app.core.theme.Golden

@Composable
fun AppNameAppBar(fontSize: Int = 20) {
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(MaterialTheme.colorScheme.inverseSurface, fontSize = fontSize.sp, fontWeight = FontWeight.W700, fontFamily = FontFamily(Font(resId = R.font.century_gothic)))) {
            append("Holy")
        }
        withStyle(style = SpanStyle(Golden, fontSize = fontSize.sp, fontWeight = FontWeight.W700, fontFamily = FontFamily(Font(resId = R.font.century_gothic)))) {
            append("Music")
        }
    }
    Text(text = annotatedString)
}