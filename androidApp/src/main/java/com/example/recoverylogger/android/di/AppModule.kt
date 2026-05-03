package com.example.recoverylogger.android.di

import android.content.Context
import com.example.recoverylogger.DatabaseDriverFactory
import com.example.recoverylogger.data.repository.EntryRepositoryImpl
import com.example.recoverylogger.db.AppDatabase
import com.example.recoverylogger.domain.repository.EntryRepository
import com.example.recoverylogger.domain.usecase.GetEntriesUseCase
import com.example.recoverylogger.domain.usecase.SaveEntryUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Provides the platform-specific SQLDelight driver factory (Android implementation)
    @Provides
    @Singleton
    fun provideDatabaseDriverFactory(@ApplicationContext context: Context): DatabaseDriverFactory =
        DatabaseDriverFactory(context)

    // Constructs AppDatabase from the driver — this is the SQLDelight-generated class
    @Provides
    @Singleton
    fun provideAppDatabase(factory: DatabaseDriverFactory): AppDatabase =
        AppDatabase(factory.createDriver())

    // Repository implementation uses the generated AppDatabase
    @Provides
    @Singleton
    fun provideEntryRepository(database: AppDatabase): EntryRepository =
        EntryRepositoryImpl(database)

    // Use cases are plain shared-module classes — Hilt provides them here
    @Provides
    @Singleton
    fun provideSaveEntryUseCase(repository: EntryRepository): SaveEntryUseCase =
        SaveEntryUseCase(repository)

    @Provides
    @Singleton
    fun provideGetEntriesUseCase(repository: EntryRepository): GetEntriesUseCase =
        GetEntriesUseCase(repository)
}
