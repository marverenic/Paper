package com.marverenic.reader.utils

import android.view.View
import android.view.ViewTreeObserver

inline fun <V : View> V.whenMeasured(crossinline action: V.() -> Unit) {
    if (isLaidOut) {
        action()
    } else {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                action()
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
}
