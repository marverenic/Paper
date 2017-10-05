package com.marverenic.reader.utils

import android.os.Parcel
import android.os.Parcelable

fun Parcel.writeBoolean(`val`: Boolean) = writeByte(if (`val`) 1 else 0)

fun Parcel.readBoolean() = readByte() != 0.toByte()

fun Parcel.writeOptionalLong(`val`: Long?) {
    writeBoolean(`val` != null)
    `val`?.let { writeLong(it) }
}

fun Parcel.readOptionalLong(): Long? = if (readBoolean()) readLong() else null

inline fun <reified T: Any> Parcel.readList()
        = mutableListOf<T>().apply { readList(this, T::class.java.classLoader) }

inline fun <reified T: Parcelable> Parcel.readParcelable(): T?
        = readParcelable(T::class.java.classLoader)

