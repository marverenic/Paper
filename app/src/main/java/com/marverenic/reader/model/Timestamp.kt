package com.marverenic.reader.model

import android.content.Context
import android.text.format.DateUtils

/**
 * Timestamps are standard Unix timestamps (in milliseconds)
 */
typealias Timestamp = Long

fun Timestamp.toDateString(context: Context)
        = DateUtils.getRelativeTimeSpanString(context, this).toString()