package com.example.recoverylogger.android.presentation.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recoverylogger.domain.usecase.GetEntriesUseCase
import com.example.recoverylogger.presentation.entry.EntryUiState
import com.example.recoverylogger.presentation.entry.EntryViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class EntryLogAndroidViewModel @Inject constructor(
    getEntriesUseCase: GetEntriesUseCase
) : ViewModel() {

    private val delegate = EntryViewModel(
        getEntriesUseCase = getEntriesUseCase,
        scope = viewModelScope
    )

    val uiState: StateFlow<EntryUiState> = delegate.uiState

    fun loadEntries() = delegate.loadEntries()
}
