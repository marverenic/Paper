package com.marverenic.reader.data.database.sql

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.marverenic.reader.utils.insert
import com.marverenic.reader.utils.insertAll
import com.marverenic.reader.utils.query
import com.marverenic.reader.utils.toList

abstract class SqliteTable<T>(private val db: SQLiteDatabase) {

    abstract val tableName: String

    protected abstract fun convertToContentValues(row: T, cv: ContentValues)

    protected abstract fun readValueFromCursor(cursor: Cursor): T

    fun insert(row: T) {
        db.insert(tableName, ContentValues().apply { convertToContentValues(row, this) })
        onInsertRow(row)
    }

    fun insertAll(rows: Collection<T>) {
        db.insertAll(tableName, rows) { item ->
            onInsertRow(item)
            ContentValues().apply { convertToContentValues(item, this) }
        }
    }

    fun queryAll() = query()

    fun query(selection: String? = null, selectionArgs: Array<String>? = null): List<T> {
        return db.query(table = tableName, selection = selection, selectionArgs = selectionArgs)
                .toList { readValueFromCursor(it) }
    }

    fun queryFirst(selection: String? = null, selectionArgs: Array<String>? = null): T? {
        db.query(table = tableName, selection = selection, selectionArgs = selectionArgs)
                .use { cursor ->
                    return if (cursor.moveToFirst()) {
                        readValueFromCursor(cursor)
                    } else {
                        null
                    }
                }
    }

    fun remove(selection: String? = null, selectionArgs: Array<String>? = null) {
        db.delete(tableName, selection, selectionArgs)
    }

    fun clear() {
        remove()
    }

    protected open fun onInsertRow(row: T) {
        // Do nothing by default.
    }

}