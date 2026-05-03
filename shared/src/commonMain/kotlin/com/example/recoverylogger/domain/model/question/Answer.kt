package com.example.recoverylogger.domain.model.question

sealed class Answer {
    data class Scale(val value: Int) : Answer()
    data class YesNo(val value: YesNoValue) : Answer()
    data class Choice(val value: String) : Answer()
    data class Text(val value: String) : Answer()
}

enum class YesNoValue {
  YES,
  NO,
  NA
}
