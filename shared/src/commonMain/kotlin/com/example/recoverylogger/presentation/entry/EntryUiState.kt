package com.example.recoverylogger.presentation.entry

data class EntryUiState(
  val entries: List<EntryUiModel> = emptyList(),
  val isLoading: Boolean = false,
  val error: String? = null
)
