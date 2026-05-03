package com.example.recoverylogger.domain.usecase

import com.example.recoverylogger.domain.common.DataResult
import com.example.recoverylogger.domain.model.entry.Entry
import com.example.recoverylogger.domain.repository.EntryRepository

class SaveEntryUseCase(private val repository: EntryRepository) {

    suspend operator fun invoke(entry: Entry): DataResult<Unit> = try {
        repository.saveEntry(entry)
        DataResult.Success(Unit)
    } catch (e: Exception) {
        DataResult.Error(e)
    }
}
