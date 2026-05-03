package com.example.recoverylogger.presentation

import com.example.recoverylogger.domain.model.entry.Entry
import com.example.recoverylogger.domain.repository.EntryRepository

class FakeEntryRepository : EntryRepository {

    var entries: List<Entry> = emptyList()
    var shouldThrow: Boolean = false
    val savedEntries: MutableList<Entry> = mutableListOf()

    override suspend fun getEntries(): List<Entry> {
        if (shouldThrow) throw Exception("Network error")
        return entries
    }

    override suspend fun saveEntry(entry: Entry) {
        if (shouldThrow) throw Exception("Network error")
        savedEntries.add(entry)
    }

    fun reset() {
        entries = emptyList()
        shouldThrow = false
        savedEntries.clear()
    }
}
