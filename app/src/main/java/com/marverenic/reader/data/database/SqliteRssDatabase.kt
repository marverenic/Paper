package com.marverenic.reader.data.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.marverenic.reader.model.*
import com.marverenic.reader.utils.*

private const val DATABASE_NAME = "feedly.db"
private const val DATABASE_VERSION = 1

private const val STREAM_TABLE_NAME = "streams"
private const val STREAM_ID_COL = "_ID"
private const val STREAM_CONTINUATION_COL = "continuation"
private const val STREAM_TITLE_COL = "title"

private const val ARTICLE_TABLE_NAME = "articles"
private const val ARTICLE_ID_COL = "_ID"
private const val ARTICLE_STREAM_ID_COL = "stream_ID"
private const val ARTICLE_TITLE_COL = "title"
private const val ARTICLE_AUTHOR_COL = "author"
private const val ARTICLE_PUBLISHED_COL = "published"
private const val ARTICLE_UPDATED_COL = "updated"
private const val ARTICLE_UNREAD_COL = "unread"
private const val ARTICLE_ORIGIN_URL_COL = "origin_url"
private const val ARTICLE_ORIGIN_TITLE_COL = "origin_title"
private const val ARTICLE_ORIGIN_STREAM_ID_COL = "origin_streamId"
private const val ARTICLE_VISUAL_WIDTH_COL = "visual_width"
private const val ARTICLE_VISUAL_HEIGHT_COL = "visual_height"
private const val ARTICLE_VISUAL_URL_COL = "visual_url"
private const val ARTICLE_CONTENT_COL = "content"

private const val CATEGORY_TABLE_NAME = "categories"
private const val CATEGORY_ID_COL = "_ID"
private const val CATEGORY_LABEL_COL = "label"

private fun Cursor.getArticle(): Article = Article(
        id = getString(ARTICLE_ID_COL),
        title = getOptionalString(ARTICLE_TITLE_COL),
        author = getOptionalString(ARTICLE_AUTHOR_COL),
        published = getLong(ARTICLE_PUBLISHED_COL),
        updated = getOptionalLong(ARTICLE_UPDATED_COL),
        unread = getBoolean(ARTICLE_UNREAD_COL),
        origin = getOrigin(),
        visual = getVisual(),
        summary = getContent(),
        alternate = null, // TODO
        tags = null, // TODO
        keywords = null // TODO
)

private fun Cursor.getOrigin(): Origin? {
    val htmlUrl = getOptionalString(ARTICLE_ORIGIN_URL_COL)
    val title = getOptionalString(ARTICLE_ORIGIN_TITLE_COL)
    val streamId = getOptionalString(ARTICLE_ORIGIN_STREAM_ID_COL)

    return if (htmlUrl == null || title == null || streamId == null) {
        null
    } else {
        Origin(htmlUrl, title, streamId)
    }
}

private fun Cursor.getVisual(): Visual? {
    val width = getOptionalInt(ARTICLE_VISUAL_WIDTH_COL)
    val height = getOptionalInt(ARTICLE_VISUAL_HEIGHT_COL)
    val url = getOptionalString(ARTICLE_VISUAL_URL_COL)

    return if (width == null || height == null || url == null) {
        null
    } else {
        Visual(width, height, url)
    }
}

private fun Cursor.getContent(): Content? {
    return getOptionalString(ARTICLE_CONTENT_COL)?.let { Content(it) }
}

private fun Cursor.getCategory(): Category = Category(
        id = getString(CATEGORY_ID_COL),
        label = getString(CATEGORY_LABEL_COL)
)

private fun ContentValues.putArticle(article: Article) {
    put(ARTICLE_ID_COL, article.id)
    put(ARTICLE_TITLE_COL, article.title)
    put(ARTICLE_AUTHOR_COL, article.author)
    put(ARTICLE_PUBLISHED_COL, article.published)
    put(ARTICLE_UPDATED_COL, article.updated)
    put(ARTICLE_UNREAD_COL, article.unread)

    article.origin?.let {
        put(ARTICLE_ORIGIN_TITLE_COL, it.title)
        put(ARTICLE_ORIGIN_STREAM_ID_COL, it.streamId)
        put(ARTICLE_ORIGIN_URL_COL, it.htmlUrl)
    }

    article.visual?.let {
        put(ARTICLE_VISUAL_WIDTH_COL, it.width)
        put(ARTICLE_VISUAL_HEIGHT_COL, it.height)
        put(ARTICLE_VISUAL_URL_COL, it.url)
    }

    article.summary?.let {
        put(ARTICLE_CONTENT_COL, it.summary())
    }
}

class SqliteRssDatabase(context: Context) : RssDatabase {
    private val databaseHelper = DatabaseHelper(context)

    private fun getArticles(streamId: String): List<Article> {
        return databaseHelper.readableDatabase
                .query(
                        table = ARTICLE_TABLE_NAME,
                        selection = "$ARTICLE_STREAM_ID_COL = ?",
                        selectionArgs = arrayOf(streamId))
                .use { cursor -> cursor.toList { it.getArticle() } }
    }

    private fun insertArticles(streamId: String, articles: List<Article>) {
        databaseHelper.writableDatabase.insertAll(ARTICLE_TABLE_NAME, articles) {
            ContentValues().apply {
                putArticle(it)
                put(ARTICLE_STREAM_ID_COL, streamId)
            }
        }
        // TODO Write lists of stuff
    }

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
                                items = getArticles(streamId)
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

        insertArticles(stream.id, stream.items)
    }

    override fun getCategories(): List<Category> {
        return databaseHelper.readableDatabase
                .query(table = CATEGORY_TABLE_NAME)
                .use { cursor -> cursor.toList { it.getCategory() } }
    }

    override fun setCategories(categories: List<Category>) {
        TODO("not implemented")
    }

    private class DatabaseHelper(context: Context)
        : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL("""
                CREATE TABLE $STREAM_TABLE_NAME (
                    $STREAM_ID_COL                      varchar     PRIMARY KEY,
                    $STREAM_CONTINUATION_COL            varchar,
                    $STREAM_TITLE_COL                   varchar
                );
            """)

            db.execSQL("""
                CREATE TABLE $ARTICLE_TABLE_NAME (
                    $ARTICLE_ID_COL                     varchar     PRIMARY KEY,
                    $ARTICLE_STREAM_ID_COL              varchar,
                    $ARTICLE_TITLE_COL                  varchar,
                    $ARTICLE_AUTHOR_COL                 varchar,
                    $ARTICLE_PUBLISHED_COL              integer,
                    $ARTICLE_UPDATED_COL                integer,
                    $ARTICLE_UNREAD_COL                 int(1),
                    $ARTICLE_ORIGIN_TITLE_COL           varchar,
                    $ARTICLE_ORIGIN_STREAM_ID_COL       varchar,
                    $ARTICLE_ORIGIN_URL_COL             varchar,
                    $ARTICLE_VISUAL_WIDTH_COL           integer,
                    $ARTICLE_VISUAL_HEIGHT_COL          integer,
                    $ARTICLE_VISUAL_URL_COL             varchar,
                    $ARTICLE_CONTENT_COL                text
                );
            """)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        }

    }

}