package com.macek.contacts.repository.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.macek.contacts.repository.db.dao.ContactDao
import com.macek.contacts.repository.db.entity.ContactEntity

@Database(
    entities = [ContactEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
}