package com.holymusic.app.features.presentation.choose_plan.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.holymusic.app.R
import com.holymusic.app.core.components.TextFieldK
import com.holymusic.app.core.theme.Primary
import com.holymusic.app.core.theme.Typography

@Composable
fun RobiPinDialogue(
    onDismiss: () -> Unit,
    mobile: String,
    pinNumberChanged: (String) -> Unit,
    proceed: (String) -> Unit,
    isValidate: Boolean,
    pin: String
){
    Dialog(onDismissRequest = { onDismiss() },
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        val mobileFocusRequester = FocusRequester()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(20.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "$mobile এই নম্বর এ ওটিপি পাঠানো হয়েছে। ওটিপি প্রবেশ করান",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 10.dp),
                style = Typography.titleMedium
            )


                TextFieldK(
                    value = pin,
                    onValueChange = { pinNumberChanged(it) },
                    focusRequester = mobileFocusRequester,
                    leadingIcon = {
                        Row(
                            modifier = Modifier.padding(start = 15.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Password, contentDescription = null)
                        }
                    },
                    label = R.string.enter_pin,
                    keyboardType = KeyboardType.Phone,
                    error = if (isValidate && pin.length != 6) stringResource(
                        id = R.string.enter_valid_pin
                    ) + " (${pin.length}/6)" else "",
                    modifier = Modifier
                        .padding(vertical = 6.dp),
                    height = 45.dp
                )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(36.dp)
                        .width(100.dp)
                        .background(color = Color(0xFFDB5A3C), shape = RoundedCornerShape(25.dp))
                        .clickable {
                            onDismiss()
                        }
                ) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        style = Typography.titleSmall
                    )
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(36.dp)
                        .width(100.dp)
                        .background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(25.dp))
                        .clickable {
                            proceed(pin)
                        }
                ) {
                    Text(
                        text = stringResource(id = R.string.continu),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        style = Typography.titleSmall
                    )
                }
            }
        }
    }
}