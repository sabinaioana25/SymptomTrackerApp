package com.example.recoverylogger.presentation.questionnaire

import com.example.recoverylogger.domain.model.entry.Entry
import com.example.recoverylogger.domain.model.question.Answer
import com.example.recoverylogger.domain.model.question.FundoplicationQuestionnaire
import com.example.recoverylogger.domain.model.question.Question

data class QuestionnaireState(
    val questions: List<Question> = FundoplicationQuestionnaire.questions,
    val currentIndex: Int = 0,
    val answers: Map<String, Answer> = emptyMap(),
    val loadingState: LoadingState = LoadingState.Idle,
    val isSubmitted: Boolean = false,
    val lastSubmittedEntry: Entry? = null,
    val error: UiError? = null
) {
    private val presenter = QuestionnairePresenter(FundoplicationQuestionnaire)

    val visibleQuestions: List<Question>
        get() = presenter.getVisibleQuestions(answers)

    val currentQuestion: Question get() = questions[currentIndex]
    val isFirstQuestion: Boolean get() = currentIndex == 0

    val isLastQuestion: Boolean
        get() = presenter.isLastQuestion(currentIndex, answers)

    val progress: Float
        get() = presenter.calculateProgress(currentIndex, answers)

    val isCurrentAnswered: Boolean
        get() = !currentQuestion.required || answers.containsKey(currentQuestion.id)

    val isSubmitting: Boolean
        get() = loadingState is LoadingState.InProgress &&
                loadingState.operation == LoadingState.Operation.SUBMITTING_ENTRY
}

sealed interface LoadingState {
    data object Idle : LoadingState
    data class InProgress(val operation: Operation) : LoadingState

    enum class Operation {
        SUBMITTING_ENTRY
    }
}

sealed interface UiError {
    val message: String

    data class NetworkError(override val message: String) : UiError
    data class ValidationError(override val message: String) : UiError
    data class OperationError(override val message: String, val operation: LoadingState.Operation) : UiError
    data class DataError(override val message: String) : UiError
}
