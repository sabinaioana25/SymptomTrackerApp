package com.example.recoverylogger.domain.model.entry

import com.example.recoverylogger.domain.model.question.Answer

data class Entry(
  val id: String,
  val userId: String,
  val entryDate: Long,
  val responses: Map<String, Answer> = emptyMap()
)
