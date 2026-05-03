package com.example.recoverylogger.presentation.questionnaire.inputs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.recoverylogger.domain.model.question.Answer
import com.example.recoverylogger.domain.model.question.Question

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun MultipleChoiceInput(
    question: Question,
    currentAnswer: Any?,
    onAnswer: (Any) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        question.options.forEach { option ->
            val selected = (currentAnswer as? Answer.Choice)?.value == option
            FilterChip(
                selected = selected,
                onClick = { onAnswer(option) },
                label = { Text(option) }
            )
        }
    }
}
