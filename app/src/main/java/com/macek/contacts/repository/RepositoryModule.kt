package com.macek.contacts.repository

import android.content.Context
import androidx.room.Room
import com.macek.contacts.repository.contacts.ContactsRepository
import com.macek.contacts.repository.contacts.ContactsRepositoryImpl
import com.macek.contacts.repository.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "AppDatabase").build()

    @Provides
    @Singleton
    fun provideContactsRepository(
        database: AppDatabase,
    ): ContactsRepository = ContactsRepositoryImpl(database.contactDao())
}