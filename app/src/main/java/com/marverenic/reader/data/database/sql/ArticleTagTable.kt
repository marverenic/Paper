package com.marverenic.reader.data.database.sql

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.marverenic.reader.utils.getString

private const val ARTICLE_TAGS_TABLE_NAME = "articleTags"
private const val ARTICLE_TAG_PAIR_ID_COL = "_ID"
private const val ARTICLE_ID_COL = "article_ID"
private const val TAG_ID_COL = "tag_ID"

private const val CREATE_STATEMENT = """
                CREATE TABLE $ARTICLE_TAGS_TABLE_NAME(
                    $ARTICLE_TAG_PAIR_ID_COL              varchar     PRIMARY KEY,
                    $ARTICLE_ID_COL                       varchar     NOT NULL,
                    $TAG_ID_COL                           varchar     NOT NULL,

                    FOREIGN KEY($ARTICLE_ID_COL)      REFERENCES $ARTICLE_TABLE_NAME,
                    FOREIGN KEY($TAG_ID_COL)          REFERENCES $TAG_TABLE_NAME
                );
    """

data class ArticleTagRow(val articleId: String, val tagId: String) {

    val id: String
        get() = "$articleId::$tagId"

}

class ArticleTagTable(db: SQLiteDatabase) : SqliteTable<ArticleTagRow>(db) {

    override val tableName = ARTICLE_TAGS_TABLE_NAME

    companion object {
        fun onCreate(db: SQLiteDatabase) {
            db.execSQL(CREATE_STATEMENT)
        }
    }

    override fun convertToContentValues(row: ArticleTagRow, cv: ContentValues) {
        cv.put(ARTICLE_TAG_PAIR_ID_COL, row.id)
        cv.put(ARTICLE_ID_COL, row.articleId)
        cv.put(TAG_ID_COL, row.tagId)
    }

    override fun readValueFromCursor(cursor: Cursor) = ArticleTagRow(
            articleId = cursor.getString(ARTICLE_ID_COL),
            tagId = cursor.getString(TAG_ID_COL)
    )

    fun insertAll(articleId: String, tagIds: List<String>) {
        insertAll(tagIds.map { ArticleTagRow(articleId, it) })
    }

    fun getTagIdsForArticle(articleId: String) = query(
                selection = "$ARTICLE_ID_COL = ?",
                selectionArgs = arrayOf(articleId))
            .map(ArticleTagRow::tagId)

}