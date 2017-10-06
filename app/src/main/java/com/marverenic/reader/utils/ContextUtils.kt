package com.marverenic.reader.utils

import android.content.Context
import android.support.annotation.AttrRes

fun Context.resolveIntAttr(@AttrRes attr: Int): Int {
    val typedArray = obtainStyledAttributes(intArrayOf(attr))
    val int = typedArray.getInt(0, 0)

    typedArray.recycle()
    return int
}
