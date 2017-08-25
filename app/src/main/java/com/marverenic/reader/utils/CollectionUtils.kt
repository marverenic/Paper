package com.marverenic.reader.utils

fun <T> List<T>.replaceAll(original: T, replacement: T): List<T> {
    return this.map { if (it == original) replacement else it }
}
