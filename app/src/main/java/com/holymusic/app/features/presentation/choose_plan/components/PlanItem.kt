package com.holymusic.app.features.presentation.choose_plan.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.holymusic.app.core.theme.BackGroundDark
import com.holymusic.app.core.theme.Gray
import com.holymusic.app.core.theme.GrayLight
import com.holymusic.app.core.theme.Primary
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.features.data.remote.entity.Plan

@Composable
fun PlanItem(plan: Plan, selectedPlan: Plan, select: (Plan) -> Unit, icon: Int) {
    Box(
        contentAlignment = Alignment.TopEnd, modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(5.dp),
            shape = RoundedCornerShape(12.dp)
        ) {

            Column(
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        color = if (plan.serviceId == selectedPlan.serviceId) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        select(plan)
                    }
                    .fillMaxWidth()
            ) {


                Row(
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier.background(
                            MaterialTheme.colorScheme.onBackground,
                            shape = RoundedCornerShape(10.dp)
                        )
                    ) {
                        Text(
                            text = "৳ ${plan.chargeAmount}",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .width(55.dp)
                                .padding(top = 3.dp),
                            textAlign = TextAlign.Center,
                            style = Typography.titleMedium
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))

                    Text(text = plan.planType, color = MaterialTheme.colorScheme.onSecondary, fontSize = 12.sp,
                        style = Typography.displaySmall)
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 5.dp)) {
                    Text(
                        text = plan.planName,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                            .weight(1f),
                        style = Typography.titleSmall
                    )
                    Image(painter = painterResource(id = icon), contentDescription = null,
                        modifier = Modifier.size(18.dp).clip(shape = CircleShape))
                }


            }

        }
        if (plan.serviceId == selectedPlan.serviceId)
            Box(modifier = Modifier.padding(end = 5.dp)) {
                Box(
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = CircleShape
                    )
                ) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(5.dp)
                            .size(18.dp)
                    )
                }
            }
    }
}