package com.holymusic.app.features.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.holymusic.app.MainActivity
import com.holymusic.app.core.theme.Primary
import com.holymusic.app.core.theme.fourHundred
import com.holymusic.app.core.theme.replaceSize
import com.holymusic.app.features.data.remote.model.PromotionsDtoItem
import com.holymusic.app.features.data.remote.model.TrackBillboardDtoItem

@Composable
fun ShortPlayDialogue(
    onDismissBillboard: () -> Unit,
    billBoard: TrackBillboardDtoItem,
    play: (TrackBillboardDtoItem) -> Unit,
    promotions: List<PromotionsDtoItem> = emptyList(),
    navToLogin: () -> Unit,
    onDismissPromotions: (Int) -> Unit,
    navToChoosePlan: () -> Unit
) {
    Dialog(onDismissRequest = { onDismissBillboard() }) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

            if (!billBoard.targetId.isNullOrEmpty())
                Box(modifier = Modifier.padding(bottom = 15.dp)) {
                    Box(modifier = Modifier.padding(12.dp)) {
                        AsyncImage(
                            model = billBoard.contentBaseUrl + replaceSize(
                                billBoard.imageUrl ?: "",
                                fourHundred
                            ), contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(shape = RoundedCornerShape(20.dp))
                                .clickable { play(billBoard) },
                            contentScale = ContentScale.FillWidth
                        )
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(shape = CircleShape)
                                .clickable { onDismissBillboard() },
                        ) {
                            Icon(
                                Icons.Filled.Close, contentDescription = null, tint = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier
                                    .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                                    .size(30.dp)
                                    .padding(2.dp)
                            )
                        }
                    }
                }

            if (promotions.isNotEmpty())
                repeat(promotions.size) {
                    Box(modifier = Modifier.padding(bottom = 15.dp)) {
                        Box(modifier = Modifier.padding(12.dp)) {
                            AsyncImage(
                                model = promotions[it].contentBaseUrl + replaceSize(
                                    promotions[it].imageUrl,
                                    fourHundred
                                ), contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(shape = RoundedCornerShape(20.dp))
                                    .clickable {
                                        if (MainActivity.isLoggedIn && !MainActivity.isPremium.value) {
                                            navToChoosePlan()
                                        }else if(!MainActivity.isLoggedIn){
                                            navToLogin()
                                        }
                                        onDismissPromotions(promotions[it].id)

                                    },
                                contentScale = ContentScale.FillWidth
                            )
                        }

                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(shape = CircleShape)
                                    .clickable { onDismissPromotions(promotions[it].id) },
                            ) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier
                                        .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                                        .size(30.dp)
                                        .padding(2.dp)
                                )
                            }
                        }
                    }
                }
        }
    }
}