package com.example.recoverylogger.domain.model.question

data class Question(
  val id: String,
  val text: String,
  val type: QuestionType,
  val required: Boolean = true,
  val order: Int,
  val rangeMin: Int? = 0,
  val rangeMax: Int? = 10,
  val options: List<String> = emptyList()
)

enum class QuestionType {
  SCALE,
  YES_NO,
  MULTIPLE_CHOICE,
  TEXT
}
