package com.example.recoverylogger.presentation.questionnaire

import com.example.recoverylogger.domain.model.question.Answer
import com.example.recoverylogger.domain.model.question.Question
import com.example.recoverylogger.domain.model.question.QuestionnaireDefinition

class QuestionnairePresenter(private val questionnaire: QuestionnaireDefinition) {
    fun getVisibleQuestions(answers: Map<String, Answer>): List<Question> {
        return questionnaire.questions.filterNot { questionnaire.shouldSkipQuestion(it.id, answers) }
    }

    fun calculateProgress(currentIndex: Int, answers: Map<String, Answer>): Float {
        val visibleQuestions = getVisibleQuestions(answers)
        val currentQuestion = questionnaire.questions.getOrNull(currentIndex) ?: return 0f
        val visibleIndex = visibleQuestions.indexOfFirst { it.id == currentQuestion.id }
            .takeIf { it >= 0 } ?: 0
        return (visibleIndex + 1).toFloat() / visibleQuestions.size.coerceAtLeast(1)
    }

    fun isLastQuestion(currentIndex: Int, answers: Map<String, Answer>): Boolean {
        for (i in questionnaire.questions.size - 1 downTo 0) {
            if (!questionnaire.shouldSkipQuestion(questionnaire.questions[i].id, answers)) {
                return currentIndex == i
            }
        }
        return currentIndex == questionnaire.questions.size - 1
    }

    fun getLastVisibleQuestionIndex(answers: Map<String, Answer>): Int {
        for (i in questionnaire.questions.size - 1 downTo 0) {
            if (!questionnaire.shouldSkipQuestion(questionnaire.questions[i].id, answers)) {
                return i
            }
        }
        return questionnaire.questions.size - 1
    }
}
