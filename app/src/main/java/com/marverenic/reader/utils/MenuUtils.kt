package com.marverenic.reader.utils

import android.view.Menu
import android.view.MenuItem

fun Menu.indices() = 0 until size()

inline fun Menu.forEach(action: (MenuItem) -> Unit) {
    indices().forEach { action(this.getItem(it)) }
}

inline fun Menu.first(condition: (MenuItem) -> Boolean): MenuItem? {
    forEach {
        if (condition(it)) {
            return it
        }
    }
    return null
}