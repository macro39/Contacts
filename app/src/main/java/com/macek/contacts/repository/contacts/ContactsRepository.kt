package com.macek.contacts.repository.contacts

import com.macek.contacts.repository.contacts.model.Contact
import kotlinx.coroutines.flow.Flow

interface ContactsRepository {
    fun getAllContacts(): Flow<List<Contact>>
    fun getFavoriteContacts(): Flow<List<Contact>>
    fun saveContact(contact: Contact)
    fun deleteContact(id: Int)
}