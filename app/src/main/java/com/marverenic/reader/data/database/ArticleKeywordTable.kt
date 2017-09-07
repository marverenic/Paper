package com.marverenic.reader.data.database

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.marverenic.reader.utils.getString

private const val ARTICLE_KEYWORD_TABLE_NAME = "articleKeywords"
private const val ARTICLE_KEYWORD_ID_COL = "_ID"
private const val ARTICLE_KEYWORD_WORD_COL = "keyword"
private const val ARTICLE_KEYWORD_ARTICLE_COL = "article_ID"

private const val CREATE_STATEMENT = """
                CREATE TABLE $ARTICLE_KEYWORD_TABLE_NAME(
                    $ARTICLE_KEYWORD_ID_COL             varchar     PRIMARY KEY,
                    $ARTICLE_KEYWORD_WORD_COL           varchar,
                    $ARTICLE_KEYWORD_ARTICLE_COL        varchar,

                    FOREIGN KEY($ARTICLE_KEYWORD_ARTICLE_COL) REFERENCES $ARTICLE_TABLE_NAME
                );
    """

data class ArticleKeywordRow(val keyword: String, val articleId: String) {

    val id: String
        get() = "$articleId/$keyword"

}

class ArticleKeywordTable(db: SQLiteDatabase) : SqliteTable<ArticleKeywordRow>(db) {

    override val tableName = ARTICLE_KEYWORD_TABLE_NAME

    companion object {
        fun onCreate(db: SQLiteDatabase) {
            db.execSQL(CREATE_STATEMENT)
        }
    }

    override fun convertToContentValues(row: ArticleKeywordRow, cv: ContentValues) {
        cv.put(ARTICLE_KEYWORD_ID_COL, row.id)
        cv.put(ARTICLE_KEYWORD_WORD_COL, row.keyword)
        cv.put(ARTICLE_KEYWORD_ARTICLE_COL, row.articleId)
    }

    override fun readValueFromCursor(cursor: Cursor) = ArticleKeywordRow(
            keyword = cursor.getString(ARTICLE_KEYWORD_WORD_COL),
            articleId = cursor.getString(ARTICLE_KEYWORD_ARTICLE_COL)
    )

    fun insert(articleId: String, keywords: Collection<String>) {
        insertAll(keywords.map { ArticleKeywordRow(it, articleId) })
    }

    fun getKeywordsForArticle(articleId: String) = query(
            selection = "$ARTICLE_KEYWORD_ARTICLE_COL = ?",
            selectionArgs = arrayOf(articleId)
    ).map(ArticleKeywordRow::keyword)

}