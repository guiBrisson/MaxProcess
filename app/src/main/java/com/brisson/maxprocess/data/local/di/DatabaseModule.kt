package com.brisson.maxprocess.data.local.di

import android.content.Context
import androidx.room.Room
import com.brisson.maxprocess.data.local.AppDatabase
import com.brisson.maxprocess.data.local.dao.ClientDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    fun providesAppDatabase(@ApplicationContext app: Context) = Room.databaseBuilder(
        app, AppDatabase::class.java, "client-db"
    ).build()

    @Singleton
    @Provides
    fun providesClientDao(db: AppDatabase): ClientDao = db.clientDao()
}
