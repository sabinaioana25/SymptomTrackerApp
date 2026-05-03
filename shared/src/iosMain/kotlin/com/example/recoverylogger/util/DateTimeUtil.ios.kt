package com.example.recoverylogger.util

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.dateWithTimeIntervalSince1970

actual fun formatDate(timestamp: Long): String {
    val date = NSDate.dateWithTimeIntervalSince1970(timestamp / 1000.0)
    val formatter = NSDateFormatter()
    formatter.dateFormat = "EEE, MMM d, yyyy HH:mm"
    formatter.locale = NSLocale.currentLocale
    return formatter.stringFromDate(date)
}
