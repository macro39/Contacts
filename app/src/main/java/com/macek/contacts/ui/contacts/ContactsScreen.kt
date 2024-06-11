package com.macek.contacts.ui.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.macek.contacts.R
import com.macek.contacts.repository.contacts.model.Contact
import com.macek.contacts.ui.compose.ContactsAppTheme
import com.macek.contacts.ui.compose.ContactsTheme
import com.macek.contacts.ui.compose.collectWithLifecycle
import com.macek.contacts.ui.compose.components.ContactItem
import com.macek.contacts.ui.compose.components.EmptyState
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun ContactsScreen(
    onAddContact: () -> Unit,
    onShowContactDetail: (Int) -> Unit,
) {
    val viewModel: ContactsViewModel = hiltViewModel<ContactsViewModel>()
    val state = viewModel.state.collectAsState()

    viewModel.event.receiveAsFlow().collectWithLifecycle { event ->
        when (event) {
            Event.AddContact -> onAddContact()
            is Event.ShowContactDetail -> onShowContactDetail(event.id)
        }
    }

    ContactsScreen(
        state = state.value,
        onSearchTextChange = { viewModel.handleAction(Action.OnSearchTextChanged(it)) },
        onAddContact = { viewModel.handleAction(Action.OnAddContact) },
        onDeleteContact = { viewModel.handleAction(Action.OnDeleteContact(it)) },
        onChangeFavoriteContact = { viewModel.handleAction(Action.OnChangeFavoriteContact(it)) },
        onContactClick = { viewModel.handleAction(Action.OnContactClick(it)) },
    )
}

@Composable
private fun ContactsScreen(
    state: State,
    onSearchTextChange: (TextFieldValue) -> Unit,
    onAddContact: () -> Unit,
    onDeleteContact: (Int) -> Unit,
    onChangeFavoriteContact: (Int) -> Unit,
    onContactClick: (Int) -> Unit,
) {
    ContactsTheme {
        Surface {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(ContactsAppTheme.spacing.xl),
                    ) {
                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = state.searchValue,
                            onValueChange = onSearchTextChange,
                            maxLines = 1,
                            placeholder = {
                                Text(text = stringResource(id = R.string.search_contacts))
                            }
                        )
                        Spacer(modifier = Modifier.height(ContactsAppTheme.spacing.l))
                        if (state.contacts.isNotEmpty()) {
                            LazyColumn {
                                items(
                                    items = state.contacts,
                                    key = { it.id }
                                ) {
                                    ContactSwipeableItem(
                                        item = it,
                                        onDelete = { onDeleteContact(it.id) },
                                        onFavoriteChange = { onChangeFavoriteContact(it.id) },
                                        onContactClick = { onContactClick(it.id) }
                                    )
                                }
                            }
                        } else {
                            EmptyState(state.emptyStateTitle)
                        }
                    }
                }
                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(ContactsAppTheme.spacing.xl),
                    onClick = { onAddContact() }
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactSwipeableItem(
    item: Contact,
    onDelete: () -> Unit,
    onFavoriteChange: () -> Unit,
    onContactClick: () -> Unit,
) {
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    true
                }

                SwipeToDismissBoxValue.StartToEnd -> {
                    onFavoriteChange()
                    false
                }

                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = state,
        backgroundContent = {
            ContactSwipeBackground(
                isFavoriteItem = item.isFavorite,
                swipeDismissState = state
            )
        },
        content = {
            ContactItem(
                item = item,
                onContactClick = onContactClick
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactSwipeBackground(
    isFavoriteItem: Boolean,
    swipeDismissState: SwipeToDismissBoxState
) {
    val backgroundColor = when (swipeDismissState.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart -> Color.Red
        SwipeToDismissBoxValue.StartToEnd -> if (isFavoriteItem) Color.Red else Color.Green
        else -> Color.Transparent
    }
    val icon = when (swipeDismissState.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
        SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Favorite
        else -> null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = ContactsAppTheme.spacing.s)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .padding(ContactsAppTheme.spacing.s),
        contentAlignment = if (swipeDismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
@Preview
private fun ContactsScreenPreview() {
    ContactsScreen(
        state = State(
            searchValue = TextFieldValue("Adam"),
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
            emptyStateTitle = "No data",
        ),
        onSearchTextChange = {},
        onAddContact = {},
        onDeleteContact = {},
        onChangeFavoriteContact = {},
        onContactClick = {},
    )
}