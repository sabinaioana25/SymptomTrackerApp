package com.example.recoverylogger.domain.model.question

interface QuestionnaireDefinition {
    val questions: List<Question>
    fun shouldSkipQuestion(questionId: String, answers: Map<String, Answer>): Boolean
}
