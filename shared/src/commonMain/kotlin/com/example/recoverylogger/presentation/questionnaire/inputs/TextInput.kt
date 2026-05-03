package com.example.recoverylogger.presentation.questionnaire.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.recoverylogger.domain.model.question.Answer

@Composable
internal fun TextInput(
    currentAnswer: Any?,
    onAnswer: (Any) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = (currentAnswer as? Answer.Text)?.value ?: "",
        onValueChange = { onAnswer(it) },
        placeholder = { Text("Type your answer here…") },
        minLines = 3,
        modifier = modifier.fillMaxWidth()
    )
}
