package com.macek.contacts.repository.contacts.model

import com.macek.contacts.repository.db.entity.ContactEntity
import com.macek.contacts.ui.favorites.model.AddFavoriteContact

fun ContactEntity.toContact() =
    Contact(
        id = id,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        isFavorite = isFavorite
    )

fun Contact.toContactEntity() =
    ContactEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        isFavorite = isFavorite
    )

fun Contact.toNewContactEntity() =
    ContactEntity(
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        isFavorite = isFavorite
    )

fun Contact.toAddFavoriteContact() =
    AddFavoriteContact(
        id = id,
        fullName = fullName,
        isSelected = false
    )