package com.marverenic.reader.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.marverenic.reader.data.database.sql.*
import com.marverenic.reader.model.Category
import com.marverenic.reader.model.Stream

private const val DATABASE_NAME = "feedly.db"
private const val DATABASE_VERSION = 1

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

    private val categoryTable = CategoryTable(writableDatabase)

    override fun getStream(streamId: String) =
            streamTable.findById(streamId)?.toStream(articleTable.findByStream(streamId))

    override fun insertStream(stream: Stream) {
        streamTable.insert(stream)
        articleTable.insertAll(stream.items, stream.id)
    }

    override fun getCategories() = categoryTable.queryAll()

    override fun setCategories(categories: List<Category>) {
        categoryTable.insertAll(categories)
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

            CategoryTable.onCreate(db)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        }

    }

}