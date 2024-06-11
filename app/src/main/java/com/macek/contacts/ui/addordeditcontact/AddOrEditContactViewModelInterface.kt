package com.macek.contacts.ui.addordeditcontact

import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow

const val ARG_CONTACT_ID = "contactId"
const val INVALID_CONTACT_ID_ARG = -1
private const val TELEPHONE_NUMBER_PREFIX = "+421"

interface AddOrEditContactViewModelInterface {
    val state: StateFlow<State>
    val event: Channel<Event>

    fun handleAction(action: Action)
}

data class State(
    val isLoading: Boolean = true,
    val title: String = "",
    val primaryButtonTitle: String = "",
    val primaryButtonEnabled: Boolean = false,
    val firstNameValue: TextFieldValue = TextFieldValue(),
    val lastNameValue: TextFieldValue = TextFieldValue(),
    val telephoneNumberValue: TextFieldValue = TextFieldValue(TELEPHONE_NUMBER_PREFIX),
)

sealed class Action {
    data class OnFirstNameTextChanged(val value: TextFieldValue) : Action()
    data class OnLastNameTextChanged(val value: TextFieldValue) : Action()
    data class OnTelephoneNumberTextChanged(val value: TextFieldValue) : Action()
    data object OnPrimaryButtonClick : Action()
    data object OnNavigateUp : Action()
}

sealed class Event {
    data object Close : Event()
}