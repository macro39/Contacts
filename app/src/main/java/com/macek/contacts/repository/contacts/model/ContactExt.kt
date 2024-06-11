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
        id,
        firstName,
        lastName,
        phoneNumber,
        isFavorite
    )

fun Contact.toAddFavoriteContact() =
    AddFavoriteContact(
        id,
        fullName,
        false
    )