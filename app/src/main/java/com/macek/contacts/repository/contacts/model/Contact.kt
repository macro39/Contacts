package com.macek.contacts.repository.contacts.model

data class Contact(
    val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val isFavorite: Boolean,
) {
    val fullName = "$firstName $lastName"
}
