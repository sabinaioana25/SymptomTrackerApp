package com.example.recoverylogger.presentation.entry

data class EntryUiModel(
    val id: String,
    val displayDate: String,
    val heartburnScore: Int?,
    val hasDifficultySwallowing: Boolean?,
    val bloatingLevel: String?,
    val hasConcerns: Boolean = false,
    val isSynced: Boolean = false,
)
