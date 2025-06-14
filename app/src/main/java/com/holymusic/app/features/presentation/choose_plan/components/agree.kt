package com.holymusic.app.features.presentation.choose_plan.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.holymusic.app.R

@Composable
fun Agree(agree: Boolean, agreeChanged: (Boolean) -> Unit) {
    val uriHandler = LocalUriHandler.current

    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.W700, fontFamily = FontFamily(
            Font(resId = R.font.century_gothic)
        ))) {
            append("I have reviewed the ")
        }
        pushStringAnnotation(tag = "policy", annotation = "http://pp.techmatrixlab.com/")
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.W700, fontFamily = FontFamily(
            Font(resId = R.font.century_gothic)
        ))) {
            append("privacy")
        }
        pop()
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.W700, fontFamily = FontFamily(Font(resId = R.font.century_gothic)))) {
            append(" and agree to the ")
        }
        pushStringAnnotation(tag = "terms", annotation = "http://tnc.techmatrixlab.com/")
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.W700, fontFamily = FontFamily(Font(resId = R.font.century_gothic)))) {
            append("terms")
        }
        pop()
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.W700, fontFamily = FontFamily(Font(resId = R.font.century_gothic)))) {
            append(" and ")
        }
        pushStringAnnotation(tag = "refund", annotation = "http://rrp.techmatrixlab.com/")
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.W700, fontFamily = FontFamily(Font(resId = R.font.century_gothic)))) {
            append("refund policies")
        }
        pop()
    }

    Row(
        modifier = Modifier.padding(start = 5.dp, end = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = agree,
            onCheckedChange = { agreeChanged(it) },
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
        )
        ClickableText(text = annotatedString, onClick = { offset ->
            annotatedString.getStringAnnotations(
                tag = "policy",
                start = offset,
                end = offset
            )
                .firstOrNull()?.let {
                    uriHandler.openUri(it.item)
                }

            annotatedString.getStringAnnotations(
                tag = "terms",
                start = offset,
                end = offset
            )
                .firstOrNull()?.let {
                    uriHandler.openUri(it.item)
                }

            annotatedString.getStringAnnotations(
                tag = "refund",
                start = offset,
                end = offset
            )
                .firstOrNull()?.let {
                    uriHandler.openUri(it.item)
                }
        })
    }
}