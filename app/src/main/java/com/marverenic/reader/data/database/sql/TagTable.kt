package com.marverenic.reader.data.database.sql

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.marverenic.reader.model.Tag
import com.marverenic.reader.utils.getString

const val TAG_TABLE_NAME = "tags"
private const val TAG_ID_COL = "_ID"
private const val TAG_LABEL_COL = "label"

private const val CREATE_STATEMENT = """
                CREATE TABLE ${TAG_TABLE_NAME}(
                    ${TAG_ID_COL}                 varchar     PRIMARY KEY,
                    ${TAG_LABEL_COL}              varchar
                );
            """

class TagTable(db: SQLiteDatabase) : SqliteTable<Tag>(db) {

    override val tableName = TAG_TABLE_NAME

    companion object {
        fun onCreate(db: SQLiteDatabase) {
            db.execSQL(CREATE_STATEMENT)
        }
    }

    override fun convertToContentValues(row: Tag, cv: ContentValues) {
        cv.put(TAG_ID_COL, row.id)
        cv.put(TAG_LABEL_COL, row.label)
    }

    override fun readValueFromCursor(cursor: Cursor) = Tag(
            id = cursor.getString(TAG_ID_COL),
            label = cursor.getString(TAG_LABEL_COL)
    )

    fun findById(tagId: String) = query(
                selection = "${TAG_ID_COL} = ?",
                selectionArgs = arrayOf(tagId))
            .firstOrNull()

}