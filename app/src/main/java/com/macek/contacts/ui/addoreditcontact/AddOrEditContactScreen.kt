package com.macek.contacts.ui.addoreditcontact

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.macek.contacts.R
import com.macek.contacts.ui.compose.ContactsAppTheme
import com.macek.contacts.ui.compose.ContactsTheme
import com.macek.contacts.ui.compose.collectWithLifecycle
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun AddOrEditContactScreen(
    onNavigateUp: () -> Unit,
) {
    val viewModel = hiltViewModel<AddOrEditContactViewModel>()
    val state = viewModel.state.collectAsState()

    viewModel.event.receiveAsFlow().collectWithLifecycle { event ->
        when (event) {
            Event.Close -> onNavigateUp()
        }
    }

    BackHandler {
        viewModel.handleAction(Action.OnNavigateUp)
    }

    AddOrEditContactScreen(
        state = state.value,
        onNavigateUp = { viewModel.handleAction(Action.OnNavigateUp) },
        onPrimaryButtonClick = { viewModel.handleAction(Action.OnPrimaryButtonClick) },
        onFirstNameTextChange = { viewModel.handleAction(Action.OnFirstNameTextChanged(it)) },
        onLastNameTextChange = { viewModel.handleAction(Action.OnLastNameTextChanged(it)) },
        onTelephoneNumberTextChange = { viewModel.handleAction(Action.OnTelephoneNumberTextChanged(it)) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddOrEditContactScreen(
    state: State,
    onNavigateUp: () -> Unit,
    onPrimaryButtonClick: () -> Unit,
    onFirstNameTextChange: (TextFieldValue) -> Unit,
    onLastNameTextChange: (TextFieldValue) -> Unit,
    onTelephoneNumberTextChange: (TextFieldValue) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    ContactsTheme {
        Surface {
            Scaffold(
                topBar = {
                    TopAppBar(
                        navigationIcon = {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .clickable { onNavigateUp() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        },
                        title = {
                            Text(text = state.title)
                        }
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(ContactsAppTheme.spacing.xl),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(ContactsAppTheme.spacing.l)
                        ) {
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = state.firstNameValue,
                                onValueChange = onFirstNameTextChange,
                                maxLines = 1,
                                label = {
                                    Text(text = stringResource(id = R.string.first_name))
                                },
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next,
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = {
                                        focusManager.moveFocus(FocusDirection.Down)
                                    }
                                )
                            )
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = state.lastNameValue,
                                onValueChange = onLastNameTextChange,
                                maxLines = 1,
                                label = {
                                    Text(text = stringResource(id = R.string.last_name))
                                },
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next,
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = {
                                        focusManager.moveFocus(FocusDirection.Down)
                                    }
                                )
                            )
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = state.telephoneNumberValue,
                                onValueChange = onTelephoneNumberTextChange,
                                maxLines = 1,
                                label = {
                                    Text(text = stringResource(id = R.string.telephone_number))
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Done,
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        if (state.primaryButtonEnabled) {
                                            onPrimaryButtonClick()
                                        }
                                    }
                                )
                            )
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = onPrimaryButtonClick,
                                enabled = state.primaryButtonEnabled
                            ) {
                                Text(text = state.primaryButtonTitle)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun AddOrEditContactScreenPreview() {
    AddOrEditContactScreen(
        state = State(
            isLoading = false,
            title = stringResource(id = R.string.add_new_contact),
            primaryButtonTitle = stringResource(id = R.string.save_contact)
        ),
        onNavigateUp = {},
        onFirstNameTextChange = {},
        onLastNameTextChange = {},
        onTelephoneNumberTextChange = {},
        onPrimaryButtonClick = {}
    )
}