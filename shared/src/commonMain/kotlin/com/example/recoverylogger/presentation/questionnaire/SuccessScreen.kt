package com.example.recoverylogger.presentation.questionnaire

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.recoverylogger.domain.model.entry.Entry
import com.example.recoverylogger.domain.model.question.Answer
import com.example.recoverylogger.domain.model.question.FundoplicationQuestionnaire
import com.example.recoverylogger.domain.model.question.YesNoValue
import com.example.recoverylogger.util.formatDate

@Composable
fun SuccessScreen(
    entry: Entry,
    onDone: () -> Unit
) {
    val questions = FundoplicationQuestionnaire.questions
    val formattedDate = formatDate(entry.entryDate)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F8FF))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "✓ Entry Submitted",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Submitted on $formattedDate",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Your Answers",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        questions.forEach { question ->
            val answer = entry.responses[question.id]
            if (answer != null) {
                AnswerCard(question = question.text, answer = answer)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Done")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun AnswerCard(question: String, answer: Any) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = question,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formatAnswer(answer),
                style = MaterialTheme.typography.bodySmall,
                color = answerColor(answer)
            )
        }
    }
}

private fun formatAnswer(answer: Any): String = when (answer) {
    is Answer.Scale -> answer.value.toString()
    is Answer.YesNo -> when (answer.value) {
        YesNoValue.YES -> "Yes"
        YesNoValue.NO -> "No"
        YesNoValue.NA -> "N/A"
    }
    is Answer.Choice -> answer.value
    is Answer.Text -> answer.value
    else -> answer.toString()
}

private fun answerColor(answer: Any): Color = when {
    answer is Answer.Scale && answer.value >= 7 -> Color(0xFFC62828)
    answer is Answer.Scale && answer.value >= 4 -> Color(0xFFF9A825)
    answer is Answer.Scale -> Color(0xFF2E7D32)
    else -> Color(0xFF424242)
}
