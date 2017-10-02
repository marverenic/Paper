package com.marverenic.reader.utils

import org.joda.time.DateTime
import org.joda.time.DateTimeZone

fun DateTime?.orEpoch() = this ?: DateTime(0, DateTimeZone.UTC)