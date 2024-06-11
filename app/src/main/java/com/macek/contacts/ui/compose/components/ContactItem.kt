package com.macek.contacts.ui.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.macek.contacts.repository.contacts.model.Contact
import com.macek.contacts.ui.compose.ContactsAppTheme

@Composable
fun ContactItem(
    item: Contact,
    onContactClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ContactsAppTheme.spacing.s)
            .border(1.dp, Color.Red, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.background)
            .clickable {
                onContactClick()
            }
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