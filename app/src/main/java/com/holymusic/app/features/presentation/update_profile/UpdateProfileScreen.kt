package com.holymusic.app.features.presentation.update_profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PeopleOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.holymusic.app.R
import com.holymusic.app.core.components.AppBar
import com.holymusic.app.core.components.ButtonK
import com.holymusic.app.core.components.TextFieldK
import com.holymusic.app.core.theme.BackGroundColor
import com.holymusic.app.core.theme.Primary
import com.holymusic.app.core.theme.Typography
import com.holymusic.app.core.util.KeyboardUnFocusHandler
import com.holymusic.app.features.presentation.update_profile.components.GenderDialogue
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UpdateProfileScreen(
    navController: NavHostController,
    viewModel: UpdateProfileViewModel = hiltViewModel()
) {

    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(id = R.string.edit_profile),
                navController = navController
            )
        }
    ) { paddingValues ->

        val state by viewModel.state.collectAsStateWithLifecycle()
        val fullNameFocusRequester = FocusRequester()
        val genderFocusRequester = FocusRequester()
        val dateOfBirthFocusRequester = FocusRequester()
        val openGenderDialog = remember { mutableStateOf(false) }
        val calendar = Calendar.getInstance()

        KeyboardUnFocusHandler()

        if (state.dateOfBirth.isEmpty()) {
            calendar.set(1990, 0, 1)
        } else {
            try {
                val date =
                    LocalDate.parse(state.dateOfBirth, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                calendar.set(date.year, date.monthValue - 1, date.dayOfMonth)
            } catch (e: Exception) {
                calendar.set(1990, 0, 1)
            }
        }

        val datePickerState =
            rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)

        val showDatePicker = remember {
            mutableStateOf(false)
        }

        if (showDatePicker.value) {
            DatePickerDialog(
                onDismissRequest = {
                    showDatePicker.value = false
                },
                confirmButton = {
                    TextButton(onClick = {
                        showDatePicker.value = false
                        viewModel.dateOfBirthChanged(datePickerState.selectedDateMillis ?: 0)
                    }) {
                        Text(text = "Confirm",
                            style = Typography.titleSmall)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDatePicker.value = false
                    }) {
                        Text(text = "Cancel",
                            style = Typography.titleSmall)
                    }
                }
            ) {
                androidx.compose.material3.DatePicker(
                    state = datePickerState
                )
            }
        }


        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp),
            ) {

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.imageUrl.isNotEmpty())
                        AsyncImage(
                            model = state.imageUrl, contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                        )
                    else
                        Box(
                            modifier = Modifier
                                .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                        ) {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(10.dp)
                            )
                        }

//                    Text(
//                        text = stringResource(id = R.string.add_a_photo),
//                        color = MaterialTheme.colorScheme.onSecondary,
//                            style = Typography.displaySmall
//                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                TextFieldK(
                    value = state.fullName,
                    label = R.string.full_name,
                    focusRequester = fullNameFocusRequester,
                    onValueChange = { viewModel.fullNameChanged(it) },
                    leadingIcon = { Icon(Icons.Filled.PersonOutline, contentDescription = null) },
                    error = if (state.isValidate && state.fullName.isEmpty()) stringResource(
                        id = R.string.enter_full_name
                    ) else "",
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                TextFieldK(
                    value = state.gender,
                    focusRequester = genderFocusRequester,
                    onValueChange = {},
                    leadingIcon = { Icon(Icons.Filled.PeopleOutline, contentDescription = null) },
                    label = R.string.gender,
                    error = if (state.isValidate && state.gender.isEmpty()) stringResource(
                        id = R.string.select_gender
                    ) else "",
                    onTap = { openGenderDialog.value = true },
                    modifier = Modifier.padding(vertical = 6.dp),
                    enabled = false
                )

                when {
                    openGenderDialog.value ->
                        GenderDialogue(
                            onDismiss = {
                                openGenderDialog.value = false
                            },
                            onSelected = {
                                viewModel.genderChanged(it)
                                openGenderDialog.value = false
                            },
                            male = stringResource(id = R.string.male),
                            female = stringResource(id = R.string.female)
                        )
                }

                TextFieldK(
                    value = state.dateOfBirth,
                    focusRequester = dateOfBirthFocusRequester,
                    onValueChange = {},
                    leadingIcon = { Icon(Icons.Filled.CalendarMonth, contentDescription = null) },
                    label = R.string.date_of_birth,
                    error = if (state.isValidate && state.dateOfBirth.isEmpty()) stringResource(
                        id = R.string.enter_date_of_birth
                    ) else "",
                    onTap = {
                        showDatePicker.value = true
                    },
                    modifier = Modifier.padding(vertical = 6.dp),
                    enabled = false
                )


                Spacer(modifier = Modifier.height(20.dp))

                ButtonK(text = R.string.save) {
                    viewModel.done(
                        fullNameFocusRequester,
                        genderFocusRequester,
                        dateOfBirthFocusRequester,
                        navController
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
