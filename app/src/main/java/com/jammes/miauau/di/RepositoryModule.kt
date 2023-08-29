package com.jammes.miauau.di

import com.jammes.miauau.core.repository.PetsRepository
import com.jammes.miauau.core.repository.PetsRepositoryFirestore
import com.jammes.miauau.core.repository.UsersRepository
import com.jammes.miauau.core.repository.UsersRepositoryFirestore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun providesPetsRepository(impl: PetsRepositoryFirestore): PetsRepository

    @Singleton
    @Binds
    abstract fun providesUsersRepository(impl: UsersRepositoryFirestore): UsersRepository
}