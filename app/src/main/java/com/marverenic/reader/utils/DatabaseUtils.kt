package com.marverenic.reader.utils

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE

fun SQLiteDatabase.query(table: String,
                         projection: Array<String>? = null,
                         selection: String? = null,
                         selectionArgs: Array<String>? = null,
                         orderBy: String? = null): Cursor
        = query(table, projection, selection, selectionArgs, null, null, orderBy)

fun SQLiteDatabase.insert(table: String, values: ContentValues) = insertWithOnConflict(table, null, values, CONFLICT_REPLACE)

inline fun <T> SQLiteDatabase.insertAll(table: String, items: Collection<T>, convert: (T) -> ContentValues) {
    items.forEach { insert(table, convert(it)) }
}

inline fun <T> SQLiteDatabase.insert(table: String, item: T, convert: (T) -> ContentValues) {
    insert(table, convert(item))
}

inline fun <T> Cursor.toList(convert: (Cursor) -> T): List<T> {
    val items = mutableListOf<T>()
    forEach {
        items += convert(this)
    }
    return items
}

inline fun Cursor.forEach(action: (Cursor) -> Unit) {
    if (moveToFirst()) {
        do {
            action(this)
        } while (moveToNext())
    }
}

fun Cursor.getString(columnName: String) = getString(getColumnIndexOrThrow(columnName))
fun Cursor.getInt(columnName: String) = getInt(getColumnIndexOrThrow(columnName))
fun Cursor.getLong(columnName: String) = getLong(getColumnIndexOrThrow(columnName))
fun Cursor.getBoolean(columnName: String) = getInt(getColumnIndexOrThrow(columnName)) != 0

fun Cursor.getOptionalString(columnName: String): String? {
    val columnIndex = getColumnIndex(columnName)
    return if (isNull(columnIndex)) null else getString(columnName)
}

fun Cursor.getOptionalInt(columnName: String): Int? {
    val columnIndex = getColumnIndex(columnName)
    return if (isNull(columnIndex)) null else getInt(columnIndex)
}

fun Cursor.getOptionalLong(columnName: String): Long? {
    val columnIndex = getColumnIndex(columnName)
    return if (isNull(columnIndex)) null else getLong(columnIndex)
}