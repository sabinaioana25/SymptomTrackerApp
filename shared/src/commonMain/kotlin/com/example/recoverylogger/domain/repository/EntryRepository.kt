package com.example.recoverylogger.domain.repository

import com.example.recoverylogger.domain.model.entry.Entry

interface EntryRepository {
    suspend fun getEntries(): List<Entry>
    suspend fun saveEntry(entry: Entry)
}
