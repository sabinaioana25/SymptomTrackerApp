package com.example.recoverylogger.presentation.questionnaire

import com.example.recoverylogger.domain.model.entry.Entry
import com.example.recoverylogger.domain.model.question.Answer
import com.example.recoverylogger.domain.model.question.FundoplicationQuestionnaire
import com.example.recoverylogger.domain.model.question.Question

data class QuestionnaireState(
    val questions: List<Question> = FundoplicationQuestionnaire.questions,
    val currentIndex: Int = 0,
    val answers: Map<String, Answer> = emptyMap(),
    val isSubmitting: Boolean = false,
    val isSubmitted: Boolean = false,
    val lastSubmittedEntry: Entry? = null,
    val error: String? = null
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
}
