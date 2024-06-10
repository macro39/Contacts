package com.macek.contacts.ui.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.macek.contacts.repository.contacts.model.Contact
import com.macek.contacts.ui.compose.ContactsAppTheme
import com.macek.contacts.ui.compose.ContactsTheme
import com.macek.contacts.ui.compose.collectWithLifecycle
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
    )
}

@Composable
private fun ContactsScreen(
    state: State,
    onSearchTextChange: (TextFieldValue) -> Unit,
    onAddContact: () -> Unit,
    onDeleteContact: (Int) -> Unit,
    onChangeFavoriteContact: (Int) -> Unit,
) {
    ContactsTheme {
        Surface {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
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
                    )
                    Spacer(modifier = Modifier.height(ContactsAppTheme.spacing.l))
                    if (state.contacts.isNotEmpty()) {
                        LazyColumn {
                            items(items = state.contacts, key = { it.id }) {
                                ContactItem(
                                    item = it,
                                    onDelete = { onDeleteContact(it.id) },
                                    onFavoriteChange = { onChangeFavoriteContact(it.id) }
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                modifier = Modifier.size(40.dp),
                                imageVector = Icons.Default.Warning,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.height(ContactsAppTheme.spacing.l))
                            Text(text = state.emptyStateDescription)
                        }
                    }
                }
                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(ContactsAppTheme.spacing.l),
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
private fun ContactItem(
    item: Contact,
    onDelete: () -> Unit,
    onFavoriteChange: () -> Unit,
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = ContactsAppTheme.spacing.s)
                    .background(MaterialTheme.colorScheme.background)
                    .border(1.dp, Color.Red, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .padding(ContactsAppTheme.spacing.s),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = if (item.isFavorite) Color.Red else Color.Black
                )
                Spacer(modifier = Modifier.width(ContactsAppTheme.spacing.s))
                Column {
                    Text(item.fullName)
                    Text(item.phoneNumber)
                }
            }
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
            .border(1.dp, Color.Red, RoundedCornerShape(20.dp))
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
            emptyStateDescription = "No data",
        ),
        {},
        {},
        {},
        {},
    )
}