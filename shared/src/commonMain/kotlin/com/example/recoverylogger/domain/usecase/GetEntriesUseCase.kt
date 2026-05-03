package com.example.recoverylogger.domain.usecase

import com.example.recoverylogger.domain.common.DataResult
import com.example.recoverylogger.domain.model.entry.Entry
import com.example.recoverylogger.domain.repository.EntryRepository

class GetEntriesUseCase(private val repository: EntryRepository) {

    suspend operator fun invoke(): DataResult<List<Entry>> = try {
        DataResult.Success(repository.getEntries())
    } catch (e: Exception) {
        DataResult.Error(e)
    }
}
