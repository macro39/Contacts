package com.macek.contacts.ui.addoreditcontact

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.macek.contacts.R
import com.macek.contacts.repository.contacts.ContactsRepository
import com.macek.contacts.repository.contacts.model.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddOrEditContactViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val contactsRepository: ContactsRepository,
) : ViewModel(), AddOrEditContactViewModelInterface {

    private val contactId = savedStateHandle.get<Int>(ARG_CONTACT_ID) ?: INVALID_CONTACT_ID_ARG

    private var isInEditMode = false

    private var currentContact: Contact? = null

    override val state = MutableStateFlow(State())

    override val event = Channel<Event>(CONFLATED)

    override fun handleAction(action: Action) {
        when (action) {
            is Action.OnFirstNameTextChanged -> onFirstNameTextChanged(action.value)
            is Action.OnLastNameTextChanged -> onLastNameTextChanged(action.value)
            is Action.OnTelephoneNumberTextChanged -> onPhoneNumberTextChanged(action.value)
            Action.OnNavigateUp -> navigateUp()
            Action.OnPrimaryButtonClick -> onPrimaryButtonClick()
        }
    }

    private fun onFirstNameTextChanged(value: TextFieldValue) {
        viewModelScope.launch {
            state.emit(
                state.value.copy(
                    firstNameValue = value
                )
            )
        }
    }

    private fun onLastNameTextChanged(value: TextFieldValue) {
        viewModelScope.launch {
            state.emit(
                state.value.copy(
                    lastNameValue = value
                )
            )
        }
    }

    private fun onPhoneNumberTextChanged(value: TextFieldValue) {
        viewModelScope.launch {
            state.emit(
                state.value.copy(
                    telephoneNumberValue = value
                )
            )
        }
    }

    private fun navigateUp() {
        viewModelScope.launch {
            event.send(Event.Close)
        }
    }

    private fun onPrimaryButtonClick() {
        val currentState = state.value
        when {
            currentState.primaryButtonEnabled && isInEditMode -> {
                currentContact?.let { contact ->
                    contactsRepository.updateContact(
                        contact.copy(
                            firstName = currentState.firstNameValue.text,
                            lastName = currentState.lastNameValue.text,
                            phoneNumber = currentState.telephoneNumberValue.text,
                        )
                    )
                    navigateUp()
                }
            }

            currentState.primaryButtonEnabled -> {
                contactsRepository.saveContact(
                    Contact(
                        firstName = currentState.firstNameValue.text,
                        lastName = currentState.lastNameValue.text,
                        phoneNumber = currentState.telephoneNumberValue.text,
                        isFavorite = false,
                    )
                )
                navigateUp()
            }
        }
    }

    init {
        isInEditMode = contactId != INVALID_CONTACT_ID_ARG

        viewModelScope.launch {
            if (isInEditMode) {
                state.emit(
                    State(
                        title = context.getString(R.string.edit_contact),
                        primaryButtonTitle = context.getString(R.string.save_changes)
                    )
                )

                contactsRepository.getContact(contactId).onEach { contact ->
                    currentContact = contact
                    contact?.let {
                        state.emit(
                            state.value.copy(
                                firstNameValue = TextFieldValue(contact.firstName),
                                lastNameValue = TextFieldValue(contact.lastName),
                                telephoneNumberValue = TextFieldValue(contact.phoneNumber),
                                isLoading = false,
                            )
                        )
                    }
                }.launchIn(viewModelScope)

                state.onEach {
                    val primaryButtonEnabled = (it.firstNameValue.text.isNotBlank() && it.firstNameValue.text != currentContact?.firstName) ||
                        (it.lastNameValue.text.isNotBlank() && it.lastNameValue.text != currentContact?.lastName) ||
                        (it.telephoneNumberValue.text.isNotBlank() && it.telephoneNumberValue.text != currentContact?.phoneNumber)

                    state.emit(
                        it.copy(
                            primaryButtonEnabled = primaryButtonEnabled
                        )
                    )
                }.launchIn(viewModelScope)
            } else {
                state.emit(
                    State(
                        title = context.getString(R.string.add_new_contact),
                        primaryButtonTitle = context.getString(R.string.save_contact),
                        isLoading = false,
                    )
                )

                state.onEach {
                    val primaryButtonEnabled = it.firstNameValue.text.isNotBlank() &&
                        it.lastNameValue.text.isNotBlank() &&
                        it.telephoneNumberValue.text.isNotBlank()

                    state.emit(
                        it.copy(
                            primaryButtonEnabled = primaryButtonEnabled
                        )
                    )
                }.launchIn(viewModelScope)
            }
        }
    }
}