package com.marverenic.reader.data.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.marverenic.reader.model.Category
import com.marverenic.reader.model.Stream
import com.marverenic.reader.utils.getString
import com.marverenic.reader.utils.query
import com.marverenic.reader.utils.toList

private const val DATABASE_NAME = "feedly.db"
private const val DATABASE_VERSION = 1

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

    private val streamTable = StreamTable(writableDatabase)
    private val linkTable = LinkTable(writableDatabase)
    private val tagTable = TagTable(writableDatabase)
    private val articleTagTable = ArticleTagTable(writableDatabase)
    private val articleKeywordsTable = ArticleKeywordTable(writableDatabase)
    private val articleTable = ArticleTable(linkTable, tagTable, articleTagTable,
            articleKeywordsTable, writableDatabase)

    override fun getStream(streamId: String) =
            streamTable.findById(streamId)?.toStream(articleTable.findByStream(streamId))

    override fun insertStream(stream: Stream) {
        streamTable.insert(stream)
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
            StreamTable.onCreate(db)
            ArticleTable.onCreate(db)
            LinkTable.onCreate(db)
            TagTable.onCreate(db)
            ArticleTagTable.onCreate(db)
            ArticleKeywordTable.onCreate(db)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        }

    }

}