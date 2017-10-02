package com.marverenic.reader.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Looper
import com.marverenic.reader.data.database.sql.*
import com.marverenic.reader.model.Article
import com.marverenic.reader.model.Category
import com.marverenic.reader.model.Stream
import com.marverenic.reader.model.toDate
import org.joda.time.DateTime

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

    private fun assertNotMainThread() {
        require(Looper.myLooper() != Looper.getMainLooper()) {
            "Database operations should not be performed on the main thread"
        }
    }

    override fun getStream(streamId: String): Stream? {
        assertNotMainThread()
        return streamTable.findById(streamId)?.let {
            val articles = articleTable.findByStream(streamId)
                    .sortedByDescending(Article::timestamp)
            return it.toStream(articles)
        }
    }

    override fun getStreamTimestamp(streamId: String): DateTime? {
        assertNotMainThread()
        return streamTable.findById(streamId)?.modified?.toDate()
    }

    override fun insertStream(stream: Stream) {
        assertNotMainThread()
        writableDatabase.beginTransaction()
        try {
            streamTable.insert(stream)
            articleTable.removeAllArticlesInStream(stream.id)
            articleTable.insertAll(stream.items, stream.id)

            writableDatabase.setTransactionSuccessful()
        } finally {
            writableDatabase.endTransaction()
        }
    }

    override fun getCategories(): List<Category> {
        assertNotMainThread()
        return categoryTable.queryAll()
    }

    override fun setCategories(categories: List<Category>) {
        assertNotMainThread()
        writableDatabase.beginTransaction()
        try {
            categoryTable.clear()
            categoryTable.insertAll(categories)

            writableDatabase.setTransactionSuccessful()
        } finally {
            writableDatabase.endTransaction()
        }
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