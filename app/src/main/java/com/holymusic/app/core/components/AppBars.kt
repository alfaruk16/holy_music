package com.holymusic.app.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.holymusic.app.R
import com.holymusic.app.core.theme.Typography

data class ActionItem(val icon: ImageVector? = null, val image: Int? = null, val action: () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String,
    titleColor: Color = MaterialTheme.colorScheme.secondary,
    icon: Int? = null,
    image: String? = null,
    navController: NavController,
    actions: List<ActionItem> = listOf(),
    isBack: Boolean = true,
    onBackPressed: (() -> Unit)? = null,
    openDrawer: (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null
) {

    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 5.dp,
        content =
            {
                TopAppBar(
                    colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            if (isBack)
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBackIos,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(start = 5.dp)
                                        .clip(shape = CircleShape)
                                        .clickable {
                                            navController.navigateUp()
                                            if (onBackPressed != null) {
                                                onBackPressed()
                                            }
                                        },
                                    tint = MaterialTheme.colorScheme.primary
                                )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) {
                                    navController.navigateUp()
                                    if (onBackPressed != null) {
                                        onBackPressed()
                                    } else if (openDrawer != null) {
                                        openDrawer()
                                    }
                                }

                            ) {
                                if (title == stringResource(id = R.string.app_name))
                                    Icon(
                                        Icons.Filled.Menu, contentDescription = null,
                                        modifier = Modifier.padding(end = 4.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )

                                if (icon != null) {

                                    Icon(
                                        painter = painterResource(id = icon),
                                        contentDescription = title,
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .fillMaxHeight()
                                            .size(24.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                if (image != null) {
                                    AsyncImage(
                                        model = image, contentDescription = null,
                                        modifier = Modifier
                                            .height(24.dp)
                                            .width(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                if (title != stringResource(id = R.string.app_name))
                                    Text(
                                        text = title,
                                        color = titleColor,
                                        style = Typography.displayLarge
                                    )

                            }

                            if (title == stringResource(id = R.string.app_name))
                                Box(
                                    modifier = Modifier.clickable(
                                        interactionSource = interactionSource,
                                        indication = null
                                    ) {
                                        if (openDrawer != null) {
                                            openDrawer()
                                        }
                                    }) {
                                    AppNameAppBar()
                                }

                            Spacer(modifier = Modifier.weight(1f))

                            for (item in actions) {
                                Box(modifier = Modifier.padding(horizontal = 5.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = MaterialTheme.colorScheme.onBackground,
                                                shape = CircleShape
                                            )
                                            .size(28.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (item.icon != null)
                                            Icon(
                                                item.icon, contentDescription = null,
                                                modifier = Modifier
                                                    .clickable {
                                                        item.action()
                                                    }
                                                    .padding(2.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        else if (item.image != null) {
                                            Icon(
                                                painter = painterResource(id = item.image),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .clickable {
                                                        item.action()
                                                    }
                                                    .padding(3.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )

                                        }
                                    }
                                }
                            }
                            if (suffix != null) {
                                Spacer(modifier = Modifier.width(10.dp))
                                suffix()
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            })
}