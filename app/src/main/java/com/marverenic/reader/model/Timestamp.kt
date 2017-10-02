package com.marverenic.reader.model

import android.content.Context
import android.text.format.DateUtils
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration

/**
 * Timestamps are standard Unix timestamps (in milliseconds)
 */
typealias Timestamp = Long

typealias Seconds = Long

fun Timestamp.toDateString(context: Context)
        = DateUtils.getRelativeTimeSpanString(context, this).toString()

fun Timestamp.toDate(): DateTime = DateTime(this, DateTimeZone.UTC)

fun Seconds.toDuration(): Duration = Duration.standardSeconds(this)
