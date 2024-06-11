package com.macek.contacts.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.macek.contacts.repository.contacts.ContactsRepository
import com.macek.contacts.repository.contacts.model.toAddFavoriteContact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val contactsRepository: ContactsRepository,
) : ViewModel(), FavoritesViewModelInterface {

    override val state = MutableStateFlow(State())

    override val event = Channel<Event>(CONFLATED)

    override fun handleAction(action: Action) {
        when (action) {
            is Action.OnShowContactDetail -> {
                viewModelScope.launch {
                    event.send(Event.ShowContactDetail(action.id))
                }
            }

            Action.OnAddFavoritesClick -> onAddFavoritesClick()

            Action.OnAddFavoritesDialogClose -> {
                viewModelScope.launch {
                    state.emit(
                        state.value.copy(
                            showAddFavoritesDialog = false,
                            saveFavoritesButtonEnabled = false,
                        )
                    )
                }
            }

            is Action.OnAddToFavoriteCheckedChange -> onAddToFavoriteCheckedChange(action.id)
            Action.OnSaveFavoritesClick -> onSaveFavoritesClick()
        }
    }

    private fun onAddFavoritesClick() {
        viewModelScope.launch {
            state.emit(
                state.value.copy(
                    addFavoriteContacts = contactsRepository.getContacts(isFavorite = false).first().map { it.toAddFavoriteContact() },
                    showAddFavoritesDialog = true,
                    saveFavoritesButtonEnabled = false,
                )
            )
        }
    }

    private fun onAddToFavoriteCheckedChange(id: Int) {
        viewModelScope.launch {
            val currentState = state.value
            val updatedAddFavoriteContacts = currentState.addFavoriteContacts.toMutableList().map {
                if (it.id == id) {
                    it.copy(isSelected = it.isSelected.not())
                } else {
                    it
                }
            }

            state.emit(
                currentState.copy(
                    addFavoriteContacts = updatedAddFavoriteContacts,
                    saveFavoritesButtonEnabled = updatedAddFavoriteContacts.any { it.isSelected }
                )
            )
        }
    }

    private fun onSaveFavoritesClick() {
        viewModelScope.launch {
            val currentState = state.value
            contactsRepository.setFavorites(currentState.addFavoriteContacts.filter { it.isSelected }.map { it.id })
            state.emit(
                currentState.copy(
                    showAddFavoritesDialog = false,
                    addFavoriteContacts = emptyList(),
                    saveFavoritesButtonEnabled = false
                )
            )
        }
    }

    init {
        contactsRepository.getContacts(isFavorite = true).onEach {
            state.emit(
                state.value.copy(
                    contacts = it
                )
            )
        }.launchIn(viewModelScope)

        contactsRepository.getContacts(isFavorite = false).onEach {
            state.emit(
                state.value.copy(
                    addMoreContactsButtonVisible = it.isNotEmpty()
                )
            )
        }.launchIn(viewModelScope)
    }
}

