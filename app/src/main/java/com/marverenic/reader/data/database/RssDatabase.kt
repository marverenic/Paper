package com.marverenic.reader.data.database

import com.marverenic.reader.model.Category
import com.marverenic.reader.model.Stream
import org.joda.time.DateTime

interface RssDatabase {

    fun getStream(streamId: String, unreadOnly: Boolean): Stream?

    fun getStreamTimestamp(streamId: String, unreadOnly: Boolean): DateTime?

    fun insertStream(stream: Stream, unreadOnly: Boolean)

    fun getCategories(): List<Category>

    fun setCategories(categories: List<Category>)

}