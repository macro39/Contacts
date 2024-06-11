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

    @Query("SELECT * FROM Contact WHERE isFavorite = :isFavorite")
    fun getContacts(isFavorite: Boolean): Flow<List<ContactEntity>>

    @Query("SELECT * FROM Contact WHERE id = :id")
    fun getContact(id: Int): Flow<ContactEntity?>

    @Query("UPDATE Contact SET isFavorite = 1 WHERE id in (:ids)")
    fun setFavorites(ids: List<Int>)

    @Query("DELETE from Contact WHERE id = :id")
    fun delete(id: Int)
}