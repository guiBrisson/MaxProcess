package com.brisson.maxprocess.data.local.di

import com.brisson.maxprocess.data.local.repository.ClientRepositoryImpl
import com.brisson.maxprocess.domain.repository.ClientRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract  class LocalRepositoryModule {
    @Binds
    abstract fun bindClientRepository(repository: ClientRepositoryImpl): ClientRepository
}
