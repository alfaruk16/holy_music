package com.holymusic.app.core.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun GridView(
    modifier: Modifier = Modifier,
    grid: Int,
    count: Int,
    content: @Composable() (index: Int) -> Unit
) {
    val columnAndRowItems = (0..<count).chunked(grid)

    Column(modifier = modifier) {
        columnAndRowItems.forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth()) {
                rowItems.forEach { index ->
                    Box(modifier = Modifier.weight(1f)) {
                        content(index)
                    }
                }
                if (grid != rowItems.size) {
                    repeat(grid - rowItems.size) {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}