package com.holymusic.app.features.presentation.update_profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.holymusic.app.R
import com.holymusic.app.core.theme.Typography

@Composable
fun GenderDialogue(
    onDismiss: () -> Unit,
    onSelected: (String) -> Unit,
    male: String,
    female: String
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {

            Text(
                text = stringResource(id = R.string.select_gender),
                style = Typography.titleMedium
            )
            Spacer(modifier = Modifier.height(20.dp))
            Card(modifier = Modifier
                .clip(shape = RoundedCornerShape(8.dp))
                .clickable {
                    onSelected(male)
                }
                .fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                Text(text = male, modifier = Modifier.padding(15.dp),
                    style = Typography.displaySmall)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Card(modifier = Modifier
                .clip(shape = RoundedCornerShape(8.dp))
                .clickable {
                    onSelected(female)
                }
                .fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                Text(text = female, modifier = Modifier.padding(15.dp),
                    style = Typography.displaySmall)
            }
        }
    }
}