package com.example.recoverylogger.presentation.questionnaire

import com.example.recoverylogger.domain.common.DataResult

fun DataResult.Error.toUiError(
    defaultMessage: String,
    operation: LoadingState.Operation? = null
): UiError {
    val message = exception.message ?: defaultMessage
    return if (operation != null) {
        UiError.OperationError(message = message, operation = operation)
    } else {
        UiError.DataError(message = message)
    }
}
