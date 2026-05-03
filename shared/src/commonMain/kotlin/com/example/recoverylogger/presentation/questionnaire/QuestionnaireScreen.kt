package com.example.recoverylogger.presentation.questionnaire

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.recoverylogger.domain.model.question.Answer
import com.example.recoverylogger.domain.model.question.Question
import com.example.recoverylogger.domain.model.question.QuestionType
import com.example.recoverylogger.domain.model.question.YesNoValue

@Composable
fun QuestionnaireScreenHoist(
    viewModel: QuestionnaireViewModel,
    onEntrySubmitted: () -> Unit,
    onBackPressed: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val navEvent by viewModel.navEvent.collectAsState()

    LaunchedEffect(navEvent) {
        when (navEvent) {
            QuestionnaireNavEvent.EntrySubmitted -> {
                onEntrySubmitted()
                viewModel.clearNavEvent()
            }
            QuestionnaireNavEvent.BackPressed -> {
                onBackPressed()
                viewModel.clearNavEvent()
            }
            null -> {}
        }
    }

    QuestionnaireScreen(
        state = state,
        onIntent = viewModel::processIntent
    )
}

@Composable
fun QuestionnaireScreen(
    state: QuestionnaireState,
    onIntent: (QuestionnaireIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Question ${state.currentIndex} of ${state.visibleQuestions.size - 1}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { state.progress },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            QuestionContent(
                question = state.currentQuestion,
                currentAnswer = state.answers[state.currentQuestion.id],
                onAnswer = { answer ->
                    onIntent(QuestionnaireIntent.Answer(state.currentQuestion.id, answer))
                }
            )
        }

        state.error?.let { msg ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = {
                    if (state.isFirstQuestion) {
                        onIntent(QuestionnaireIntent.Back)
                    } else {
                        onIntent(QuestionnaireIntent.Previous)
                    }
                },
                enabled = true
            ) {
                Text(if (state.isFirstQuestion) "Cancel" else "Back")
            }

            if (state.isLastQuestion) {
                Button(
                    onClick = { onIntent(QuestionnaireIntent.Submit) },
                    enabled = state.isCurrentAnswered && !state.isSubmitting
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .height(18.dp)
                                .width(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text("Submit")
                    }
                }
            } else {
                Button(
                    onClick = { onIntent(QuestionnaireIntent.Next) },
                    enabled = state.isCurrentAnswered
                ) {
                    Text("Next")
                }
            }
        }
    }
}

@Composable
private fun QuestionContent(
    question: Question,
    currentAnswer: Any?,
    onAnswer: (Any) -> Unit
) {
    Column {
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
            QuestionType.SCALE           -> ScaleInput(question, currentAnswer, onAnswer)
            QuestionType.YES_NO          -> YesNoInput(currentAnswer, onAnswer)
            QuestionType.MULTIPLE_CHOICE -> MultipleChoiceInput(question, currentAnswer, onAnswer)
            QuestionType.TEXT            -> TextInput(currentAnswer, onAnswer)
        }
    }
}

@Composable
private fun ScaleInput(
    question: Question,
    currentAnswer: Any?,
    onAnswer: (Any) -> Unit
) {
    val min = question.rangeMin ?: 1
    val max = question.rangeMax ?: 10
    val value = (currentAnswer as? Answer.Scale)?.value ?: min

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("$min", style = MaterialTheme.typography.bodySmall)
            Text(
                text = "$value",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text("$max", style = MaterialTheme.typography.bodySmall)
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onAnswer(it.toInt()) },
            valueRange = min.toFloat()..max.toFloat(),
            steps = (max - min - 1).coerceAtLeast(0),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun YesNoInput(currentAnswer: Any?, onAnswer: (Any) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MultipleChoiceInput(
    question: Question,
    currentAnswer: Any?,
    onAnswer: (Any) -> Unit
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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

@Composable
private fun TextInput(currentAnswer: Any?, onAnswer: (Any) -> Unit) {
    OutlinedTextField(
        value = (currentAnswer as? Answer.Text)?.value ?: "",
        onValueChange = { onAnswer(it) },
        placeholder = { Text("Type your answer here…") },
        minLines = 3,
        modifier = Modifier.fillMaxWidth()
    )
}
