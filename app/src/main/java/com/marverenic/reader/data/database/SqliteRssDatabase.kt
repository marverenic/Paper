package com.marverenic.reader.data.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.marverenic.reader.model.Category
import com.marverenic.reader.model.Stream
import com.marverenic.reader.utils.*

private const val DATABASE_NAME = "feedly.db"
private const val DATABASE_VERSION = 1

private const val STREAM_TABLE_NAME = "streams"
private const val STREAM_ID_COL = "_ID"
private const val STREAM_CONTINUATION_COL = "continuation"
private const val STREAM_TITLE_COL = "title"

private const val CATEGORY_TABLE_NAME = "categories"
private const val CATEGORY_ID_COL = "_ID"
private const val CATEGORY_LABEL_COL = "label"

private fun Cursor.getCategory(): Category = Category(
        id = getString(CATEGORY_ID_COL),
        label = getString(CATEGORY_LABEL_COL)
)

class SqliteRssDatabase(context: Context) : RssDatabase {

    private val databaseHelper = DatabaseHelper(context)
    private val writableDatabase: SQLiteDatabase
        get() = databaseHelper.writableDatabase

    private val linkTable = LinkTable(writableDatabase)
    private val tagTable = TagTable(writableDatabase)
    private val articleTagTable = ArticleTagTable(writableDatabase)
    private val articleTable = ArticleTable(linkTable, tagTable, articleTagTable, writableDatabase)

    override fun getStream(streamId: String): Stream? {
        databaseHelper.readableDatabase
                .query(
                        table = STREAM_TABLE_NAME,
                        selection = "$STREAM_ID_COL = ?",
                        selectionArgs = arrayOf(streamId)
                )
                .use {
                    return if (it.moveToFirst()) {
                        Stream(
                                id = it.getString(STREAM_ID_COL),
                                continuation = it.getOptionalString(STREAM_CONTINUATION_COL),
                                title = it.getOptionalString(STREAM_TITLE_COL),
                                items = articleTable.findByStream(streamId)
                        )
                    } else {
                        null
                    }
                }
    }

    override fun insertStream(stream: Stream) {
        databaseHelper.writableDatabase
                .insert(STREAM_TABLE_NAME, stream) {
                    ContentValues().apply {
                        put(STREAM_ID_COL, it.id)
                        put(STREAM_TITLE_COL, it.title)
                        put(STREAM_CONTINUATION_COL, it.continuation)
                    }
                }

        articleTable.insertAll(stream.items, stream.id)
    }

    override fun getCategories(): List<Category> {
        return databaseHelper.readableDatabase
                .query(table = CATEGORY_TABLE_NAME)
                .use { cursor -> cursor.toList { it.getCategory() } }
    }

    override fun setCategories(categories: List<Category>) {
        TODO("not implemented")
    }

    private inner class DatabaseHelper(context: Context)
        : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
            ArticleTable.onCreate(db)
            LinkTable.onCreate(db)
            TagTable.onCreate(db)
            ArticleTagTable.onCreate(db)

            db.execSQL("""
                CREATE TABLE $STREAM_TABLE_NAME (
                    $STREAM_ID_COL                      varchar     PRIMARY KEY,
                    $STREAM_CONTINUATION_COL            varchar,
                    $STREAM_TITLE_COL                   varchar
                );
            """)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        }

    }

}