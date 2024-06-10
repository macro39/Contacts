package com.macek.contacts.ui.contacts

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.macek.contacts.R
import com.macek.contacts.repository.contacts.ContactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val contactsRepository: ContactsRepository,
) : ViewModel(), ContactsViewModelInterface {

    override val state = MutableStateFlow(State(emptyStateDescription = context.getString(R.string.you_do_not_have_any_contacts)))

    override val event = Channel<Event>(CONFLATED)

    override fun handleAction(action: Action) {
        when (action) {
            is Action.OnSearchTextChanged -> onSearchTextChanged(action.value)
            is Action.OnChangeFavoriteContact -> onChangeFavoriteContact(action.id)
            is Action.OnDeleteContact -> onDelete(action.id)
            Action.OnAddContact -> {
                viewModelScope.launch {
                    event.send(Event.AddContact)
                }
            }

            is Action.OnShowContactDetail -> {
                viewModelScope.launch {
                    event.send(Event.ShowContactDetail(action.id))
                }
            }
        }
    }

    private fun onChangeFavoriteContact(id: Int) {
        state.value.contacts.firstOrNull { it.id == id }?.let { contactItem ->
            contactsRepository.saveContact(contactItem.copy(isFavorite = contactItem.isFavorite.not()))
        }
    }

    private fun onDelete(id: Int) {
        contactsRepository.deleteContact(id)
    }

    private fun onSearchTextChanged(value: TextFieldValue) {
        viewModelScope.launch {
            state.emit(state.value.copy(searchValue = value))
        }
    }

    init {
        combine(
            contactsRepository.getAllContacts(),
            state
        ) { contacts, state ->
            val filteredContacts = contacts.filter {
                it.firstName.contains(state.searchValue.text, true) ||
                    it.lastName.contains(state.searchValue.text, true) ||
                    it.phoneNumber.contains(state.searchValue.text, true)
            }
            val emptyStateDescription = if (filteredContacts.isEmpty() && state.searchValue.text.isEmpty()) R.string.no_contacts_found_change_your_search_text else R.string.no_contacts_found_change_your_search_text

            this.state.emit(
                state.copy(
                    contacts = filteredContacts,
                    emptyStateDescription = context.getString(emptyStateDescription),
                )
            )
        }.launchIn(viewModelScope)
    }
}
