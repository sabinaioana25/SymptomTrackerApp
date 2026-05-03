package com.example.recoverylogger

import com.example.recoverylogger.data.repository.EntryRepositoryImpl
import com.example.recoverylogger.db.AppDatabase
import com.example.recoverylogger.domain.usecase.GetEntriesUseCase
import com.example.recoverylogger.domain.usecase.SaveEntryUseCase

object IosServiceLocator {
    private val database: AppDatabase by lazy {
        AppDatabase(DatabaseDriverFactory().createDriver())
    }
    private val entryRepository by lazy { EntryRepositoryImpl(database) }

    val getEntriesUseCase: GetEntriesUseCase by lazy { GetEntriesUseCase(entryRepository) }
    val saveEntryUseCase: SaveEntryUseCase by lazy { SaveEntryUseCase(entryRepository) }
}
