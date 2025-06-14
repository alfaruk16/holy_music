package com.holymusic.app.features.presentation.choose_plan.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.holymusic.app.core.components.GridView
import com.holymusic.app.core.theme.Gray
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.features.data.remote.entity.Plan
import com.holymusic.app.features.presentation.choose_plan.PaymentOptions

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun OnlinePayment(
    sections: List<PaymentOptions>,
    modifier: Modifier = Modifier,
    selectedPlan: Plan,
    selectedPayment: String,
    selectPayment: (String) -> Unit
) {
    FlowColumn(modifier) {
        sections.forEachIndexed { i, _ ->
            if (sections[i].plans.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                        .clickable { selectPayment(sections[i].operator) }
                        .height(50.dp),
                    elevation = CardDefaults.cardElevation(5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
                    ) {
                        Image(
                            painterResource(id = sections[i].image),
                            contentDescription = null,
                            modifier = Modifier.fillMaxHeight(),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = sections[i].title,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.weight(1f),
                            style = Typography.titleSmall
                        )
                        Icon(
                            Icons.Default.run {
                                if (selectedPayment != sections[i].operator)
                                    KeyboardArrowDown
                                else
                                    KeyboardArrowUp
                            },
                            contentDescription = "",
                            tint = Color.LightGray,
                        )
                    }
                }
                if (selectedPayment == sections[i].operator) {
                    GridView(
                        grid = 2, count = sections[i].plans.size,
                        modifier = Modifier.padding(horizontal = 5.dp)
                    ) { j ->
                        PlanItem(
                            plan = sections[i].plans[j],
                            selectedPlan = selectedPlan,
                            select = { sections[i].selected(sections[i].plans[j]) },
                            icon = sections[i].icon
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }
    }
}
