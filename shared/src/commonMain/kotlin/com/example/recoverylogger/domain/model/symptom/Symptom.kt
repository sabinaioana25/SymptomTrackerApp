package com.example.recoverylogger.domain.model.symptom

data class Symptom(
  val id: String,
  val name: String,
  val severity: Int,
  val notes: String?
)
