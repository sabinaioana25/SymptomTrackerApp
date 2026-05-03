package com.example.recoverylogger.presentation.questionnaire.inputs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.recoverylogger.domain.model.question.Answer
import com.example.recoverylogger.domain.model.question.YesNoValue

@Composable
internal fun YesNoInput(
    currentAnswer: Any?,
    onAnswer: (Any) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listOf("Yes", "No").forEach { option ->
            val answerValue = when (currentAnswer) {
                is Answer.YesNo -> when (currentAnswer.value) {
                    YesNoValue.YES -> "Yes"
                    YesNoValue.NO -> "No"
                    YesNoValue.NA -> null
                }
                else -> null
            }
            val selected = answerValue == option
            Button(
                onClick = { onAnswer(option) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (selected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(option)
            }
        }
    }
}
