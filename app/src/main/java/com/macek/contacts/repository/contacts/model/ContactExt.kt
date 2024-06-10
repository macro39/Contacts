package com.macek.contacts.repository.contacts.model

import com.macek.contacts.repository.db.entity.ContactEntity

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