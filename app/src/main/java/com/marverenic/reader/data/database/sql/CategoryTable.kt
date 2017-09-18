package com.marverenic.reader.data.database.sql

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.marverenic.reader.model.Category
import com.marverenic.reader.utils.getString

private const val CATEGORY_TABLE_NAME = "categories"
private const val CATEGORY_ID_COL = "_ID"
private const val CATEGORY_LABEL_COL = "label"

private const val CREATE_STATEMENT = """
                CREATE TABLE ${CATEGORY_TABLE_NAME}(
                    ${CATEGORY_ID_COL}                    varchar     PRIMARY KEY,
                    ${CATEGORY_LABEL_COL}                 varchar
                );
            """

class CategoryTable(db: SQLiteDatabase) : SqliteTable<Category>(db) {

    override val tableName = CATEGORY_TABLE_NAME

    companion object {
        fun onCreate(db: SQLiteDatabase) {
            db.execSQL(CREATE_STATEMENT)
        }
    }

    override fun convertToContentValues(row: Category, cv: ContentValues) {
        cv.apply {
            put(CATEGORY_ID_COL, row.id)
            put(CATEGORY_LABEL_COL, row.label)
        }
    }

    override fun readValueFromCursor(cursor: Cursor) = Category(
            id = cursor.getString(CATEGORY_ID_COL),
            label = cursor.getString(CATEGORY_LABEL_COL)
    )

}