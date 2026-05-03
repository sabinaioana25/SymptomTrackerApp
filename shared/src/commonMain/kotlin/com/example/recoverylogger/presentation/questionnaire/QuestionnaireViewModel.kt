package com.example.recoverylogger.presentation.questionnaire

import com.example.recoverylogger.domain.common.DataResult
import com.example.recoverylogger.domain.model.entry.Entry
import com.example.recoverylogger.domain.model.question.Answer
import com.example.recoverylogger.domain.model.question.FundoplicationQuestionnaire
import com.example.recoverylogger.domain.model.question.QuestionType
import com.example.recoverylogger.domain.model.question.YesNoValue
import com.example.recoverylogger.domain.usecase.SaveEntryUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class QuestionnaireViewModel(
    private val saveEntryUseCase: SaveEntryUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private val _state = MutableStateFlow(QuestionnaireState())
    val state: StateFlow<QuestionnaireState> = _state.asStateFlow()

    fun processIntent(intent: QuestionnaireIntent) {
        when (intent) {
            is QuestionnaireIntent.Answer    -> recordAnswer(intent.questionId, intent.value)
            is QuestionnaireIntent.Next      -> goToNext()
            is QuestionnaireIntent.Previous  -> goToPrevious()
            is QuestionnaireIntent.Submit    -> submitEntry()
            is QuestionnaireIntent.Back      -> Unit
            is QuestionnaireIntent.DismissError -> _state.update { it.copy(error = null) }
        }
    }

    private fun recordAnswer(questionId: String, value: Any) {
        val question = FundoplicationQuestionnaire.questions.find { it.id == questionId } ?: return
        val answer = when (question.type) {
            QuestionType.SCALE -> Answer.Scale(value as Int)
            QuestionType.YES_NO -> {
                val yesNoValue = when (value) {
                    "Yes" -> YesNoValue.YES
                    "No" -> YesNoValue.NO
                    else -> YesNoValue.NA
                }
                Answer.YesNo(yesNoValue)
            }
            QuestionType.MULTIPLE_CHOICE -> Answer.Choice(value as String)
            QuestionType.TEXT -> Answer.Text(value as String)
        }
        _state.update { it.copy(answers = it.answers + (questionId to answer)) }
    }

    private fun goToNext() {
        val s = _state.value
        if (!s.isCurrentAnswered) return
        if (!s.isLastQuestion) {
            var nextIndex = s.currentIndex + 1
            while (nextIndex < s.questions.size &&
                   FundoplicationQuestionnaire.shouldSkipQuestion(s.questions[nextIndex].id, s.answers)) {
                nextIndex++
            }
            if (nextIndex < s.questions.size) {
                _state.update { it.copy(currentIndex = nextIndex) }
            }
        }
    }

    private fun goToPrevious() {
        val s = _state.value
        if (!s.isFirstQuestion) {
            var prevIndex = s.currentIndex - 1
            while (prevIndex >= 0 &&
                   FundoplicationQuestionnaire.shouldSkipQuestion(s.questions[prevIndex].id, s.answers)) {
                prevIndex--
            }
            if (prevIndex >= 0) {
                _state.update { it.copy(currentIndex = prevIndex) }
            }
        }
    }

    @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
    private fun submitEntry() {
        val s = _state.value
        if (s.isSubmitting) return

        val entry = Entry(
            id = Uuid.random().toString(),
            userId = "anonymous",
            entryDate = Clock.System.now().toEpochMilliseconds(),
            responses = s.answers
        )

        scope.launch {
            _state.update {
                it.copy(
                    loadingState = LoadingState.InProgress(LoadingState.Operation.SUBMITTING_ENTRY),
                    error = null
                )
            }
            when (val result = saveEntryUseCase(entry)) {
                is DataResult.Success -> _state.update {
                    it.copy(
                        loadingState = LoadingState.Idle,
                        isSubmitted = true,
                        lastSubmittedEntry = entry
                    )
                }
                is DataResult.Error -> _state.update {
                    it.copy(
                        loadingState = LoadingState.Idle,
                        error = result.toUiError(
                            defaultMessage = "Failed to save entry",
                            operation = LoadingState.Operation.SUBMITTING_ENTRY
                        )
                    )
                }
            }
        }
    }
}
