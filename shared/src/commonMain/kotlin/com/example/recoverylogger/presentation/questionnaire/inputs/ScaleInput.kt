package com.example.recoverylogger.presentation.questionnaire.inputs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.recoverylogger.domain.model.question.Answer
import com.example.recoverylogger.domain.model.question.Question

@Composable
internal fun ScaleInput(
    question: Question,
    currentAnswer: Any?,
    onAnswer: (Any) -> Unit,
    modifier: Modifier = Modifier
) {
    val min = question.rangeMin ?: 1
    val max = question.rangeMax ?: 10
    val value = (currentAnswer as? Answer.Scale)?.value ?: min

    Column(modifier = modifier) {
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
