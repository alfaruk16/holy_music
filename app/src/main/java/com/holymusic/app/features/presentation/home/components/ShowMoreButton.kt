package com.holymusic.app.features.presentation.home.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.holymusic.app.R
import com.holymusic.app.core.theme.BorderColor
import com.holymusic.app.core.theme.Primary
import com.holymusic.app.core.theme.Typography

@Composable
fun ShowMoreButton(isShowMore: Boolean, showMore: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .border(
                width = 0.8.dp,
                color = MaterialTheme.colorScheme.surfaceBright,
                shape = RoundedCornerShape(10.dp)
            )
            .clip(shape = RoundedCornerShape(10.dp))
            .clickable {
                showMore()
            }.testTag("showMore"), contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = if (isShowMore) R.string.show_more else R.string.show_less),
                color = MaterialTheme.colorScheme.primary,
                style = Typography.titleSmall
            )
            Icon(
                if (isShowMore) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}