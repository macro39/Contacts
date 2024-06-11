package com.macek.contacts.ui.favorites

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.macek.contacts.R
import com.macek.contacts.repository.contacts.model.Contact
import com.macek.contacts.ui.compose.ContactsAppTheme
import com.macek.contacts.ui.compose.ContactsTheme
import com.macek.contacts.ui.compose.collectWithLifecycle
import com.macek.contacts.ui.compose.components.ContactItem
import com.macek.contacts.ui.compose.components.EmptyState
import com.macek.contacts.ui.favorites.model.AddFavoriteContact
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun FavoritesScreen(
    onShowContactDetail: (Int) -> Unit,
) {
    val viewModel = hiltViewModel<FavoritesViewModel>()
    val state = viewModel.state.collectAsState()

    viewModel.event.receiveAsFlow().collectWithLifecycle { event ->
        when (event) {
            is Event.ShowContactDetail -> onShowContactDetail(event.id)
        }
    }

    FavoritesScreen(
        state = state.value,
        onContactClick = { viewModel.handleAction(Action.OnShowContactDetail(it)) },
        onAddFavoritesClick = { viewModel.handleAction(Action.OnAddFavoritesClick) },
        onAddFavoritesDialogClose = { viewModel.handleAction(Action.OnAddFavoritesDialogClose) },
        onAddToFavoriteCheckedChange = { viewModel.handleAction(Action.OnAddToFavoriteCheckedChange(it)) },
        onSaveFavorites = { viewModel.handleAction(Action.OnSaveFavoritesClick) },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FavoritesScreen(
    state: State,
    onAddFavoritesClick: () -> Unit,
    onAddFavoritesDialogClose: () -> Unit,
    onContactClick: (Int) -> Unit,
    onAddToFavoriteCheckedChange: (Int) -> Unit,
    onSaveFavorites: () -> Unit,
) {
    ContactsTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ContactsAppTheme.spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    if (state.contacts.isNotEmpty()) {
                        LazyColumn {
                            stickyHeader {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.background)
                                        .padding(bottom = ContactsAppTheme.spacing.l),
                                    text = stringResource(id = R.string.favorite_contacts_pattern, state.contacts.size)
                                )
                            }
                            items(
                                items = state.contacts,
                                key = { it.id }
                            ) {
                                ContactItem(
                                    item = it,
                                    onContactClick = {
                                        onContactClick(it.id)
                                    }
                                )
                            }
                        }
                    } else {
                        EmptyState(title = stringResource(id = R.string.you_do_not_have_any_favorite_contacts))
                    }
                }
                if (state.addMoreContactsButtonVisible) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onAddFavoritesClick
                    ) {
                        Text(text = stringResource(id = R.string.add_more_favorites))
                    }
                }
            }
        }

        if (state.showAddFavoriteDialog) {
            AddFavoritesDialog(
                items = state.addFavoriteContacts,
                saveFavoritesButtonEnabled = state.saveFavoritesButtonEnabled,
                onDismiss = onAddFavoritesDialogClose,
                onAddToFavoriteCheckedChange = onAddToFavoriteCheckedChange,
                onSaveFavorites = onSaveFavorites
            )
        }
    }
}

@Composable
private fun AddFavoritesDialog(
    items: List<AddFavoriteContact>,
    saveFavoritesButtonEnabled: Boolean,
    onAddToFavoriteCheckedChange: (Int) -> Unit,
    onDismiss: () -> Unit,
    onSaveFavorites: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ContactsAppTheme.spacing.xl),
            shape = RoundedCornerShape(size = 20.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(20.dp))
                    .padding(ContactsAppTheme.spacing.xl)
            ) {
                LazyColumn {
                    items(
                        items = items,
                        key = { "${it.id}-${it.isSelected}" }
                    ) {
                        AddFavoriteContactItem(
                            item = it,
                            onCheckedChange = {
                                onAddToFavoriteCheckedChange(it.id)
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(ContactsAppTheme.spacing.l))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onSaveFavorites,
                    enabled = saveFavoritesButtonEnabled
                ) {
                    Text(text = stringResource(id = R.string.save_favorites))
                }
            }
        }
    }
}

@Composable
private fun AddFavoriteContactItem(
    item: AddFavoriteContact,
    onCheckedChange: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isSelected,
            onCheckedChange = { onCheckedChange() }
        )
        Spacer(modifier = Modifier.width(ContactsAppTheme.spacing.l))
        Text(text = item.fullName)
    }
}

@Composable
@Preview
private fun FavoritesScreenPreview() {
    FavoritesScreen(
        state = State(
            contacts = listOf(
                Contact(
                    0,
                    "Adam",
                    "Piatrik",
                    "+4216415",
                    false
                ),
                Contact(
                    1,
                    "Peter",
                    "Opl",
                    "+4216415",
                    false
                ),
                Contact(
                    2,
                    "Kiril",
                    "Namukov",
                    "+4216415",
                    false
                )
            ),
            addFavoriteContacts = emptyList(),
            showAddFavoriteDialog = false,
            addMoreContactsButtonVisible = true
        ),
        onContactClick = {},
        onAddFavoritesClick = {},
        onAddFavoritesDialogClose = {},
        onAddToFavoriteCheckedChange = {},
        onSaveFavorites = {}
    )
}
