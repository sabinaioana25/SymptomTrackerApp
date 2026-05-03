package com.example.recoverylogger

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import com.example.recoverylogger.presentation.entry.EntryLogScreen
import com.example.recoverylogger.presentation.entry.EntryViewModel
import com.example.recoverylogger.presentation.questionnaire.QuestionnaireScreen
import com.example.recoverylogger.presentation.questionnaire.QuestionnaireViewModel
import com.example.recoverylogger.presentation.questionnaire.SuccessScreen

private enum class Screen { Questionnaire, Success, EntryLog }

fun MainViewController() = ComposeUIViewController {
    val questionnaireVm = remember {
        QuestionnaireViewModel(IosServiceLocator.saveEntryUseCase)
    }
    val entryVm = remember {
        EntryViewModel(IosServiceLocator.getEntriesUseCase)
    }

    val state by questionnaireVm.state.collectAsState()
    var screen by remember { mutableStateOf(Screen.Questionnaire) }

    LaunchedEffect(state.isSubmitted) {
        if (state.isSubmitted) screen = Screen.Success
    }

    when (screen) {
        Screen.Questionnaire -> QuestionnaireScreen(
            state = state,
            onIntent = questionnaireVm::processIntent
        )
        Screen.Success -> {
            val entry = state.lastSubmittedEntry
            if (entry != null) {
                SuccessScreen(
                    entry = entry,
                    onDone = { screen = Screen.EntryLog }
                )
            }
        }
        Screen.EntryLog -> EntryLogScreen(
            viewModel = entryVm,
            onBack = { screen = Screen.Questionnaire }
        )
    }
}
