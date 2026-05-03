package com.example.recoverylogger.domain.repository

import com.example.recoverylogger.domain.model.symptom.Symptom

interface SymptomRepository {
  suspend fun saveSymptom(symptom: Symptom)
  suspend fun getAllSymptoms(): List<Symptom>
}
