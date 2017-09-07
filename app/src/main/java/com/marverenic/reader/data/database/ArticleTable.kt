package com.marverenic.reader.data.database

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.marverenic.reader.model.*
import com.marverenic.reader.utils.*

const val ARTICLE_TABLE_NAME = "articles"
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

private const val CREATE_STATEMENT = """
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
            """

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

data class ArticleRow(val article: Article, val streamId: String)

class ArticleTable(private val linkTable: LinkTable,
                   private val tagTable: TagTable,
                   private val articleTagTable: ArticleTagTable,
                   private val articleKeywordTable: ArticleKeywordTable,
                   db: SQLiteDatabase) : SqliteTable<ArticleRow>(db) {

    override val tableName = "articles"

    companion object {
        fun onCreate(db: SQLiteDatabase) {
            db.execSQL(CREATE_STATEMENT)
        }
    }

    override fun convertToContentValues(row: ArticleRow, cv: ContentValues) {
        cv.put(ARTICLE_STREAM_ID_COL, row.streamId)

        val article = row.article

        cv.put(ARTICLE_ID_COL, article.id)
        cv.put(ARTICLE_TITLE_COL, article.title)
        cv.put(ARTICLE_AUTHOR_COL, article.author)
        cv.put(ARTICLE_PUBLISHED_COL, article.published)
        cv.put(ARTICLE_UPDATED_COL, article.updated)
        cv.put(ARTICLE_UNREAD_COL, article.unread)

        article.origin?.let {
            cv.put(ARTICLE_ORIGIN_TITLE_COL, it.title)
            cv.put(ARTICLE_ORIGIN_STREAM_ID_COL, it.streamId)
            cv.put(ARTICLE_ORIGIN_URL_COL, it.htmlUrl)
        }

        article.visual?.let {
            cv.put(ARTICLE_VISUAL_WIDTH_COL, it.width)
            cv.put(ARTICLE_VISUAL_HEIGHT_COL, it.height)
            cv.put(ARTICLE_VISUAL_URL_COL, it.url)
        }

        article.summary?.let {
            cv.put(ARTICLE_CONTENT_COL, it.summary())
        }
    }

    override fun readValueFromCursor(cursor: Cursor): ArticleRow {
        val articleId = cursor.getString(ARTICLE_ID_COL)
        return ArticleRow(
                streamId = cursor.getString(ARTICLE_STREAM_ID_COL),
                article = Article(
                        id = articleId,
                        title = cursor.getOptionalString(ARTICLE_TITLE_COL),
                        author = cursor.getOptionalString(ARTICLE_AUTHOR_COL),
                        published = cursor.getLong(ARTICLE_PUBLISHED_COL),
                        updated = cursor.getOptionalLong(ARTICLE_UPDATED_COL),
                        unread = cursor.getBoolean(ARTICLE_UNREAD_COL),
                        origin = cursor.getOrigin(),
                        visual = cursor.getVisual(),
                        summary = cursor.getContent(),
                        alternate = linkTable.findByArticle(articleId),
                        tags = articleTagTable.getTagIdsForArticle(articleId)
                                .mapNotNull { tagTable.findById(it) },
                        keywords = articleKeywordTable.getKeywordsForArticle(articleId)
                )
        )
    }

    fun insert(article: Article, streamId: String) {
        insert(ArticleRow(article, streamId))
    }

    fun insertAll(articles: Collection<Article>, streamId: String) {
        insertAll(articles.map { ArticleRow(it, streamId) })
    }

    fun findByStream(streamId: String) = query(
                selection = "$ARTICLE_STREAM_ID_COL = ?",
                selectionArgs = arrayOf(streamId))
            .map(ArticleRow::article)

    override fun onInsertRow(row: ArticleRow) {
        val article = row.article;
        article.alternate?.let {
            linkTable.insertAll(it, row.article.id)
        }

        article.tags?.let {
            tagTable.insertAll(it)
            articleTagTable.insertAll(article.id, it.map(Tag::id))
        }

        article.keywords?.let {
            articleKeywordTable.insert(article.id, it)
        }
    }

}