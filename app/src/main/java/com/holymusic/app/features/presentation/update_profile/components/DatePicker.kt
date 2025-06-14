package com.holymusic.app.features.presentation.update_profile.components

import android.widget.CalendarView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog

@Composable
fun DatePicker(
    onDismiss: () -> Unit,
    onSelected: (String) -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            AndroidView(
                { CalendarView(it) },
                modifier = Modifier.wrapContentWidth(),
                update = { views ->
                    views.setOnDateChangeListener { _, i, i2, i3 ->
                        onSelected(i3.toString() + "-" + (i2 + 1).toString() + "-" + i.toString())
                    }
                }
            )
        }
    }
}