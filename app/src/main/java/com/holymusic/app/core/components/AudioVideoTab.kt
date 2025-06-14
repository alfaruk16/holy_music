package com.holymusic.app.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.core.util.AppConstants

@Composable
fun AudioVideoTab(selectedTab: Int, tabChanged: (Int) -> Unit) {
    val list = remember {
        listOf(AppConstants.audio, AppConstants.video)
    }

    TabRow(
        selectedTabIndex = selectedTab,
        divider = {},
        indicator = {},
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 5.dp),
        containerColor = MaterialTheme.colorScheme.background
    ) {
        list.forEachIndexed { index, text ->
            Box(
                modifier = Modifier
                    .padding(
                        horizontal = 5.dp
                    )
                    .background(
                        Color.Transparent,
                        shape = RoundedCornerShape(25.dp)
                    )
            ) {
                Tab(
                    selected = selectedTab == index,
                    onClick = { tabChanged(index) },
                    text = {
                        Text(
                            text = text,
                            style = Typography.displaySmall,
                            color = if (selectedTab == index) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier
                        .background(
                            if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(50.dp)
                        )
                        .height(30.dp)
                        .border(
                            .5.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(50.dp)
                        )

                )
            }
        }
    }
}