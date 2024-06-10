package com.macek.contacts.repository.contacts

import com.macek.contacts.repository.contacts.model.Contact
import com.macek.contacts.repository.contacts.model.toContact
import com.macek.contacts.repository.contacts.model.toContactEntity
import com.macek.contacts.repository.db.dao.ContactDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ContactsRepositoryImpl(
    private val contactDao: ContactDao
) : ContactsRepository {

    override fun getAllContacts(): Flow<List<Contact>> = contactDao.getAll().map {
        it.map {
            it.toContact()
        }
    }

    override fun getFavoriteContacts(): Flow<List<Contact>> = contactDao.getAllFavoriteContacts().map {
        it.map {
            it.toContact()
        }
    }

    override fun saveContact(contact: Contact) {
        CoroutineScope(Dispatchers.IO).launch {
            contactDao.upsert(contact.toContactEntity())
        }
    }

    override fun deleteContact(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            contactDao.delete(id)
        }
    }
}