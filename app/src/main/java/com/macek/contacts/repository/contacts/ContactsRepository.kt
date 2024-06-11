package com.macek.contacts.repository.contacts

import com.macek.contacts.repository.contacts.model.Contact
import kotlinx.coroutines.flow.Flow

interface ContactsRepository {
    fun getAllContacts(): Flow<List<Contact>>
    fun getContacts(isFavorite: Boolean): Flow<List<Contact>>
    fun getContact(id: Int): Flow<Contact?>
    fun saveContact(contact: Contact)
    fun updateContact(contact: Contact)
    fun setFavorites(ids: List<Int>)
    fun deleteContact(id: Int)
}