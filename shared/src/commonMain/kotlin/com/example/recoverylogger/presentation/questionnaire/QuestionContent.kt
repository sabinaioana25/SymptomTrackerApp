package com.example.recoverylogger.presentation.questionnaire

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.recoverylogger.domain.model.question.Question
import com.example.recoverylogger.domain.model.question.QuestionType
import com.example.recoverylogger.presentation.questionnaire.inputs.MultipleChoiceInput
import com.example.recoverylogger.presentation.questionnaire.inputs.ScaleInput
import com.example.recoverylogger.presentation.questionnaire.inputs.TextInput
import com.example.recoverylogger.presentation.questionnaire.inputs.YesNoInput

@Composable
fun QuestionContent(
    question: Question,
    currentAnswer: Any?,
    onAnswer: (Any) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = question.text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        if (!question.required) {
            Text(
                text = "Optional",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        when (question.type) {
            QuestionType.SCALE -> ScaleInput(question, currentAnswer, onAnswer)
            QuestionType.YES_NO -> YesNoInput(currentAnswer, onAnswer)
            QuestionType.MULTIPLE_CHOICE -> MultipleChoiceInput(question, currentAnswer, onAnswer)
            QuestionType.TEXT -> TextInput(currentAnswer, onAnswer)
        }
    }
}
