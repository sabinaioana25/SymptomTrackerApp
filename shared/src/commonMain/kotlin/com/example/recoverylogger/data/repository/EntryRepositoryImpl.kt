package com.example.recoverylogger.data.repository

import com.example.recoverylogger.db.AppDatabase
import com.example.recoverylogger.db.Recovery_entry
import com.example.recoverylogger.domain.model.entry.Entry
import com.example.recoverylogger.domain.model.question.Answer
import com.example.recoverylogger.domain.model.question.YesNoValue
import com.example.recoverylogger.domain.repository.EntryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Clock

class EntryRepositoryImpl(private val database: AppDatabase) : EntryRepository {

    override suspend fun getEntries(): List<Entry> = withContext(Dispatchers.Default) {
        database.entryQueries.getAllEntries().executeAsList().map { it.toDomain() }
    }

    override suspend fun saveEntry(entry: Entry): Unit = withContext(Dispatchers.Default) {
        database.entryQueries.insertEntry(
            id = entry.id,
            userId = entry.userId,
            entryDate = entry.entryDate,
            createdAt = Clock.System.now().toEpochMilliseconds(),
            q1Heartburn = (entry.responses["q1_heartburn"] as? Answer.Scale)?.value?.toLong(),
            q2Swallowing = when (entry.responses["q2_swallowing"]) {
                is Answer.YesNo -> when ((entry.responses["q2_swallowing"] as Answer.YesNo).value) {
                    YesNoValue.YES -> "Yes"
                    YesNoValue.NO -> "No"
                    YesNoValue.NA -> "N/A"
                }
                else -> null
            },
            q3SwallowingSeverity = (entry.responses["q3_swallowing_severity"] as? Answer.Scale)?.value?.toLong(),
            q4Vomit = (entry.responses["q4_vomit"] as? Answer.Choice)?.value,
            q5Bloating = (entry.responses["q5_bloating"] as? Answer.Choice)?.value,
            q6Diet = (entry.responses["q6_diet"] as? Answer.Text)?.value,
            q7Medications = (entry.responses["q7_medications"] as? Answer.Text)?.value,
            q8Concerns = (entry.responses["q8_concerns"] as? Answer.Text)?.value
        )
    }

    override suspend fun deleteEntry(entry: String) {
      TODO("Not yet implemented")
    }
}

// ---------------------------------------------------------------------------
// Mapping: SQLDelight Recovery_entry → domain Entry
// ---------------------------------------------------------------------------
private fun Recovery_entry.toDomain() = Entry(
    id = id,
    userId = userId,
    entryDate = entryDate,
    responses = buildMap {
        q1Heartburn?.let { put("q1_heartburn", Answer.Scale(it.toInt())) }
        q2Swallowing?.let { put("q2_swallowing", Answer.YesNo(when (it) {
            "Yes" -> YesNoValue.YES
            "No" -> YesNoValue.NO
            else -> YesNoValue.NA
        })) }
        q3SwallowingSeverity?.let { put("q3_swallowing_severity", Answer.Scale(it.toInt())) }
        q4Vomit?.let { put("q4_vomit", Answer.Choice(it)) }
        q5Bloating?.let { put("q5_bloating", Answer.Choice(it)) }
        q6Diet?.let { put("q6_diet", Answer.Text(it)) }
        q7Medications?.let { put("q7_medications", Answer.Text(it)) }
        q8Concerns?.let { put("q8_concerns", Answer.Text(it)) }
    }
)
