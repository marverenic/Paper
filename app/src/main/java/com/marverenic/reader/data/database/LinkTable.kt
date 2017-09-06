package com.marverenic.reader.data.database

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.marverenic.reader.model.Link
import com.marverenic.reader.utils.getString

private const val LINK_TABLE_NAME = "links"
private const val LINK_ID_COL = "_ID"
private const val LINK_HREF_COL = "href"
private const val LINK_TYPE_COL = "type"
private const val LINK_ARTICLE_ID_COL = "article_ID"

private const val CREATE_STATEMENT = """
                CREATE TABLE $LINK_TABLE_NAME(
                    $LINK_ID_COL                    varchar     PRIMARY KEY,
                    $LINK_HREF_COL                  varchar,
                    $LINK_TYPE_COL                  varchar,
                    $LINK_ARTICLE_ID_COL            varchar
                );
            """

data class LinkRow(val link: Link, val articleId: String) {

    val id: String
        get() = "$articleId/${link.href}"

}

class LinkTable(db: SQLiteDatabase) : SqliteTable<LinkRow>(db) {

    override val tableName = LINK_TABLE_NAME

    companion object {
        fun onCreate(db: SQLiteDatabase) {
            db.execSQL(CREATE_STATEMENT)
        }
    }

    override fun convertToContentValues(row: LinkRow, cv: ContentValues) {
        cv.put(LINK_ID_COL, row.id)
        cv.put(LINK_ARTICLE_ID_COL, row.articleId)

        val link = row.link

        cv.put(LINK_HREF_COL, link.href)
        cv.put(LINK_TYPE_COL, link.type)
    }

    override fun readValueFromCursor(cursor: Cursor) = LinkRow(
            link = Link(
                    href = cursor.getString(LINK_HREF_COL),
                    type = cursor.getString(LINK_TYPE_COL)
            ),
            articleId = cursor.getString(LINK_ARTICLE_ID_COL)
    )

    fun insert(link: Link, articleId: String) {
        insert(LinkRow(link, articleId))
    }

    fun insertAll(links: Collection<Link>, articleId: String) {
        insertAll(links.map { LinkRow(it, articleId) })
    }

    fun findByArticle(articleId: String) = query(
                selection = "$LINK_ARTICLE_ID_COL = ?",
                selectionArgs = arrayOf(articleId))
            .map(LinkRow::link)

}