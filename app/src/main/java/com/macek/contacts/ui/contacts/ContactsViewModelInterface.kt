package com.macek.contacts.ui.contacts

import androidx.compose.ui.text.input.TextFieldValue
import com.macek.contacts.repository.contacts.model.Contact
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow

interface ContactsViewModelInterface {
    val state: StateFlow<State>
    val event: Channel<Event>

    fun handleAction(action: Action)
}

data class State(
    val searchValue: TextFieldValue = TextFieldValue(),
    val contacts: List<Contact> = emptyList(),
    val emptyStateTitle: String,
)

sealed class Action {
    data class OnSearchTextChanged(val value: TextFieldValue) : Action()
    data class OnDeleteContact(val id: Int) : Action()
    data class OnChangeFavoriteContact(val id: Int) : Action()
    data object OnAddContact : Action()
    data class OnContactClick(val id: Int) : Action()
}

sealed class Event {
    data object AddContact : Event()
    data class ShowContactDetail(val id: Int) : Event()
}