package com.example.recoverylogger.presentation.questionnaire

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

@Composable
fun QuestionnaireScreenHoist(
    viewModel: QuestionnaireViewModel,
    onEntrySubmitted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val onIntent: (QuestionnaireIntent) -> Unit = viewModel::processIntent

    LaunchedEffect(state.isSubmitted) {
        if (state.isSubmitted) onEntrySubmitted()
    }

    QuestionnaireScreen(
        state = state,
        onIntent = onIntent,
        modifier = modifier
    )
}
