package com.marverenic.reader.data.database.sql

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.marverenic.reader.model.Article
import com.marverenic.reader.model.Stream
import com.marverenic.reader.model.Timestamp
import com.marverenic.reader.utils.*
import org.joda.time.DateTime

private const val STREAM_TABLE_NAME = "streams"
private const val STREAM_ID_COL = "_ID"
private const val STREAM_CONTINUATION_COL = "continuation"
private const val STREAM_TITLE_COL = "title"
private const val STREAM_MODIFIED_COL = "modified"
private const val STREAM_UNREAD_ONLY_COL = "unread_only"

private const val CREATE_STATEMENT = """
                CREATE TABLE $STREAM_TABLE_NAME (
                    $STREAM_ID_COL                          varchar,
                    $STREAM_CONTINUATION_COL                varchar,
                    $STREAM_TITLE_COL                       varchar,
                    $STREAM_MODIFIED_COL                    integer,
                    $STREAM_UNREAD_ONLY_COL                 int(1),
                    PRIMARY KEY($STREAM_ID_COL, $STREAM_UNREAD_ONLY_COL)
                );
            """

data class StreamMetadata(
        val id: String,
        val continuation: String?,
        val title: String?,
        val modified: Timestamp,
        val unreadOnly: Boolean
) {

    fun toStream(entries: List<Article>) = Stream(
            id = id,
            continuation = continuation,
            title = title,
            items = entries
    )

}

class StreamTable(db: SQLiteDatabase) : SqliteTable<StreamMetadata>(db) {

    override val tableName = STREAM_TABLE_NAME

    companion object {
        fun onCreate(db: SQLiteDatabase) {
            db.execSQL(CREATE_STATEMENT)
        }
    }

    override fun convertToContentValues(row: StreamMetadata, cv: ContentValues) {
        cv.apply {
            put(STREAM_ID_COL, row.id)
            put(STREAM_CONTINUATION_COL, row.continuation)
            put(STREAM_TITLE_COL, row.title)
            put(STREAM_MODIFIED_COL, row.modified)
            put(STREAM_UNREAD_ONLY_COL, row.unreadOnly)
        }
    }

    override fun readValueFromCursor(cursor: Cursor) = StreamMetadata(
            id = cursor.getString(STREAM_ID_COL),
            continuation = cursor.getOptionalString(STREAM_CONTINUATION_COL),
            title = cursor.getOptionalString(STREAM_TITLE_COL),
            modified = cursor.getLong(STREAM_MODIFIED_COL),
            unreadOnly = cursor.getBoolean(STREAM_UNREAD_ONLY_COL)
    )

    fun insert(stream: Stream, unreadOnly: Boolean) {
        val now = DateTime.now().millis
        insert(StreamMetadata(stream.id, stream.continuation, stream.title, now, unreadOnly))
    }

    fun findById(id: String, unreadOnly: Boolean) = queryFirst(
            selection = "$STREAM_ID_COL = ? AND $STREAM_UNREAD_ONLY_COL = ?",
            selectionArgs = arrayOf(id, unreadOnly.toInt().toString())
    )

}