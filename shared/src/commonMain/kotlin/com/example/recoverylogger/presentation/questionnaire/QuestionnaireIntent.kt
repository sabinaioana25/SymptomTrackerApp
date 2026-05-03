package com.example.recoverylogger.presentation.questionnaire

sealed interface QuestionnaireIntent {
    data class Answer(val questionId: String, val value: Any) : QuestionnaireIntent
    data object Next : QuestionnaireIntent
    data object Previous : QuestionnaireIntent
    data object Submit : QuestionnaireIntent
    data object Back : QuestionnaireIntent
    data object DismissError : QuestionnaireIntent
}

sealed interface QuestionnaireNavEvent {
    data object EntrySubmitted : QuestionnaireNavEvent
    data object BackPressed : QuestionnaireNavEvent
}
