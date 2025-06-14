package com.holymusic.app.features.presentation.downloads.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.holymusic.app.R
import com.holymusic.app.core.theme.Primary
import com.holymusic.app.core.theme.Typography

@Composable
fun DeleteConfirmation(
    delete: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(20.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(id = R.string.delete_this_item),
                color = Primary,
                modifier = Modifier.padding(bottom = 20.dp),
                style = Typography.titleMedium
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(36.dp)
                        .width(100.dp)
                        .background(color = Primary, shape = RoundedCornerShape(25.dp))
                        .clip(shape = RoundedCornerShape(25.dp))
                        .clickable {
                            onDismiss()
                        }
                ) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        style = Typography.titleSmall
                    )
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(36.dp)
                        .width(100.dp)
                        .background(color = Color(0xFFDB5A3C), shape = RoundedCornerShape(25.dp))
                        .clip(shape = RoundedCornerShape(25.dp)).clickable {
                            delete()
                        }
                ) {
                    Text(
                        text = stringResource(id = R.string.delete),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        style = Typography.titleSmall
                    )
                }
            }
        }
    }
}