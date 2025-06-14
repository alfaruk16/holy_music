package com.holymusic.app.features.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.holymusic.app.core.theme.Primary
import com.holymusic.app.R
import androidx.compose.ui.platform.LocalUriHandler


const val playStoreLink =
    "https://play.google.com/store/apps/details?id=com.v4technologiesbd.holy_tune"
const val alQuranLink = "https://play.google.com/store/apps/details?id=com.muslimbd.al_quran"
const val duaLink = "https://play.google.com/store/apps/details?id=com.muslimbd.dua"
const val zakatLink = "https://play.google.com/store/apps/details?id=com.muslimbd.zakat"

@Composable
fun PlayStore() {

    val uriHandler = LocalUriHandler.current

    Box(modifier = Modifier.padding(horizontal = 10.dp)) {
        Column {
            Row(
                Modifier
                    .clickable { uriHandler.openUri(playStoreLink) }
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(15)
                    ),
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.muslim_bd_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .padding(10.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(15))
                )
                Text(
                    text = stringResource(id = R.string.play_store_text),
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Companion.Center
                )
                Spacer(modifier = Modifier.width(10.dp))
                Image(
                    painter = painterResource(id = R.drawable.play_store_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .padding(10.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(15))
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                PlayStoreItem(
                    image = R.drawable.al_quran_icon,
                    name = stringResource(id = R.string.al_quran_bn),
                    onTap = { uriHandler.openUri(alQuranLink) }
                )
                PlayStoreItem(
                    image = R.drawable.dua,
                    name = stringResource(id = R.string.dua_bn),
                    onTap = { uriHandler.openUri(duaLink) }
                )
                PlayStoreItem(
                    image = R.drawable.zakat,
                    name = stringResource(id = R.string.zakat_bn),
                    onTap = { uriHandler.openUri(zakatLink) }
                )
            }
        }
    }
}

@Composable
fun PlayStoreItem(image: Int, name: String, onTap: () -> Unit) {
    Box(contentAlignment = Alignment.Companion.BottomEnd) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clickable {
                    onTap()
                }
                .shadow(
                    10.dp,
                    spotColor = MaterialTheme.colorScheme.primary,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                ),
        )

        Column(
            modifier = Modifier
                .size(100.dp)
                .background(
                    Color.Companion.White,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                ),
            horizontalAlignment = Alignment.Companion.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = image), contentDescription = null,
                contentScale = ContentScale.Companion.Fit,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = name, fontSize = 14.sp, fontWeight = FontWeight.Companion.W600,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}