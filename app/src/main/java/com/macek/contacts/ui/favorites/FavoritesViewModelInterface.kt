package com.macek.contacts.ui.favorites

import com.macek.contacts.repository.contacts.model.Contact
import com.macek.contacts.ui.favorites.model.AddFavoriteContact
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow

interface FavoritesViewModelInterface {
    val state: StateFlow<State>
    val event: Channel<Event>

    fun handleAction(action: Action)
}

data class State(
    val contacts: List<Contact> = emptyList(),
    val addFavoriteContacts: List<AddFavoriteContact> = emptyList(),
    val saveFavoritesButtonEnabled: Boolean = false,
    val showAddFavoriteDialog: Boolean = false,
    val addMoreContactsButtonVisible: Boolean = false,
)

sealed class Action {
    data class OnShowContactDetail(val id: Int) : Action()
    data object OnAddFavoritesClick : Action()
    data object OnAddFavoritesDialogClose : Action()
    data class OnAddToFavoriteCheckedChange(val id: Int) : Action()
    data object OnSaveFavoritesClick : Action()
}

sealed class Event {
    data class ShowContactDetail(val id: Int) : Event()
}