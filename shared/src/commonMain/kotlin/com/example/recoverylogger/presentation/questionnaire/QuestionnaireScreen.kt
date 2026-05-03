package com.example.recoverylogger.presentation.questionnaire

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnaireScreen(
    state: QuestionnaireState,
    onIntent: (QuestionnaireIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackBarHostState.showSnackbar(error.message)
            onIntent(QuestionnaireIntent.DismissError)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
}
