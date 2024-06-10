package com.macek.contacts.repository.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.macek.contacts.repository.db.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(contact: ContactEntity)

    @Query("SELECT * FROM Contact")
    fun getAll(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM Contact WHERE isFavorite = 1")
    fun getAllFavoriteContacts(): Flow<List<ContactEntity>>

    @Query("DELETE from Contact WHERE id = :id")
    fun delete(id: Int)
}