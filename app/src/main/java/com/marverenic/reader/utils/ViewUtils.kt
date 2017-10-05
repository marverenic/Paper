package com.marverenic.reader.utils

import android.view.View

inline fun <V : View> V.whenMeasured(crossinline action: V.() -> Unit) {
    if (isLaidOut) {
        action()
    } else {
        viewTreeObserver.addOnGlobalLayoutListener {
            action()
        }
    }
}
