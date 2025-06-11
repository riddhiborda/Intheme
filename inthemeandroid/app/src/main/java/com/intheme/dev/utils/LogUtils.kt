package com.intheme.dev.utils

import android.util.Log
import com.intheme.dev.BuildConfig

const val LOG_ERROR_TAG = "[logError]"
inline fun Throwable.logError(causeProvider: Throwable.() -> Throwable = { this }) {
    val cause = this.causeProvider()
    if (BuildConfig.DEBUG) {
        Log.e(LOG_ERROR_TAG, this.message, cause)
    }
}

const val LOG_TRACE_REQUIRED = true
fun String.loge(tag: String = "--->E") {
    if (LOG_TRACE_REQUIRED && isNotEmpty() && BuildConfig.DEBUG) {
       val maxLogSize = 4000
       for (i in indices step maxLogSize) {
          val end = if (i + maxLogSize < length) i + maxLogSize else length
          Log.e(tag, substring(i, end))
       }
    }
}


fun String.logD(tag: String = "--->D") {
    if (LOG_TRACE_REQUIRED && isNotEmpty() && BuildConfig.DEBUG ) {
        val maxLogSize = 4000
        for (i in indices step maxLogSize) {
            val end = if (i + maxLogSize < length) i + maxLogSize else length
            Log.d(tag, substring(i, end))
        }
    }
}

fun String.logi(tag: String = "--->I") {
    if (LOG_TRACE_REQUIRED && isNotEmpty() && BuildConfig.DEBUG ) {
        val maxLogSize = 4000
        for (i in indices step maxLogSize) {
            val end = if (i + maxLogSize < length) i + maxLogSize else length
            Log.i(tag, substring(i, end))
        }
    }
}