package com.example.recoverylogger.domain.model.question

object FundoplicationQuestionnaire : QuestionnaireDefinition {
  override val questions = listOf(
    Question(
      id = "q1_heartburn",
      text = "How is your heartburn today?",
      type = QuestionType.SCALE,
      order = 1,
      rangeMin = 1,
      rangeMax = 10
    ),
    Question(
      id = "q2_swallowing",
      text = "Difficulty swallowing?",
      type = QuestionType.YES_NO,
      order = 2
    ),
    Question(
      id = "q3_swallowing_severity",
      text = "If yes, rate severity",
      type = QuestionType.SCALE,
      order = 3,
      rangeMin = 1,
      rangeMax = 10,
      required = false
    ),
    Question(
      id = "q4_vomit",
      text = "Able to vomit if needed?",
      type = QuestionType.MULTIPLE_CHOICE,
      options = listOf("Yes", "No", "N/A"),
      order = 4
    ),
    Question(
      id = "q5_bloating",
      text = "Bloating or gas?",
      type = QuestionType.MULTIPLE_CHOICE,
      options = listOf("None", "Mild", "Moderate", "Severe"),
      order = 5
    ),
    Question(
      id = "q6_diet",
      text = "Diet notes (what did you eat?)",
      type = QuestionType.TEXT,
      order = 6,
      required = false
    ),
    Question(
      id = "q7_medications",
      text = "Medications taken today",
      type = QuestionType.TEXT,
      order = 7,
      required = false
    ),
  )

  override fun shouldSkipQuestion(questionId: String, answers: Map<String, Answer>): Boolean {
    return if (questionId == "q3_swallowing_severity") {
      answers["q2_swallowing"] != Answer.YesNo(YesNoValue.YES)
    } else {
      false
    }
  }
}
