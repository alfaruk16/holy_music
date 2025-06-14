package com.holymusic.app.features.presentation.login.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.holymusic.app.core.theme.Typography

@Composable
fun FacebookLoginButton(
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.clickable(
            onClick = onClick
        ),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 18.dp)
        ) {
            Icon(
                Icons.Filled.Facebook,
                contentDescription = "Login with Facebook",
                tint = Color.Blue
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Login with Facebook",
                style = Typography.displaySmall
            )
        }
    }
}
