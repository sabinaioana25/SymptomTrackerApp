package com.example.recoverylogger.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("EEE, MMM d, yyyy HH:mm", Locale.getDefault())
    return format.format(date)
}
