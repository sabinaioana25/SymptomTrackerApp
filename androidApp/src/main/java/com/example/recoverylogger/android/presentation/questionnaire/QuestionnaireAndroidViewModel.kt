package com.example.recoverylogger.android.presentation.questionnaire

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recoverylogger.domain.usecase.SaveEntryUseCase
import com.example.recoverylogger.presentation.questionnaire.QuestionnaireIntent
import com.example.recoverylogger.presentation.questionnaire.QuestionnaireState
import com.example.recoverylogger.presentation.questionnaire.QuestionnaireViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class QuestionnaireAndroidViewModel @Inject constructor(
    saveEntryUseCase: SaveEntryUseCase
) : ViewModel() {

    val viewModel: QuestionnaireViewModel = QuestionnaireViewModel(
        saveEntryUseCase = saveEntryUseCase,
        scope = viewModelScope
    )

    val state: StateFlow<QuestionnaireState> = viewModel.state
    fun processIntent(intent: QuestionnaireIntent) = viewModel.processIntent(intent)

}
