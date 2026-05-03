package com.example.recoverylogger.presentation.entry

import com.example.recoverylogger.domain.common.DataResult
import com.example.recoverylogger.domain.model.entry.Entry
import com.example.recoverylogger.domain.model.question.Answer
import com.example.recoverylogger.domain.model.question.YesNoValue
import com.example.recoverylogger.domain.usecase.GetEntriesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class EntryViewModel(
    private val getEntriesUseCase: GetEntriesUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {

    private val _uiState = MutableStateFlow(EntryUiState())
    val uiState: StateFlow<EntryUiState> = _uiState.asStateFlow()

    init {
        loadEntries()
    }

    fun loadEntries() {
        scope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = getEntriesUseCase()) {
                is DataResult.Success -> _uiState.update {
                    it.copy(entries = result.data.map { entry -> entry.toUiModel() }, isLoading = false)
                }
                is DataResult.Error -> _uiState.update {
                    it.copy(error = result.exception.message ?: "Unknown error", isLoading = false)
                }
            }
        }
    }
}

fun Entry.toUiModel(): EntryUiModel {
    val local = Instant.fromEpochMilliseconds(entryDate)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val day = local.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    val month = local.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }

    return EntryUiModel(
        id = id,
        displayDate = "$day ${local.dayOfMonth} $month",
        heartburnScore = (responses["q1_heartburn"] as? Answer.Scale)?.value,
        hasDifficultySwallowing = when (responses["q2_swallowing"]) {
            is Answer.YesNo -> when ((responses["q2_swallowing"] as Answer.YesNo).value) {
                YesNoValue.YES -> true
                YesNoValue.NO -> false
                YesNoValue.NA -> null
            }
            else -> null
        },
        bloatingLevel = (responses["q5_bloating"] as? Answer.Choice)?.value,
        hasConcerns = (responses["q8_concerns"] as? Answer.Text)?.value?.isNotBlank() == true,
        isSynced = false,
    )
}
