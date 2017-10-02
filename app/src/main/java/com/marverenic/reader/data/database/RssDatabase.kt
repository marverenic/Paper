package com.marverenic.reader.data.database

import com.marverenic.reader.model.Category
import com.marverenic.reader.model.Stream
import org.joda.time.DateTime

interface RssDatabase {

    fun getStream(streamId: String): Stream?

    fun getStreamTimestamp(streamId: String): DateTime?

    fun insertStream(stream: Stream)

    fun getCategories(): List<Category>

    fun setCategories(categories: List<Category>)

}