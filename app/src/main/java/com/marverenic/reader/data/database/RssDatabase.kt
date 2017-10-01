package com.marverenic.reader.data.database

import com.marverenic.reader.model.Category
import com.marverenic.reader.model.Stream

interface RssDatabase {

    fun getStream(streamId: String): Stream?

    fun insertStream(stream: Stream)

    fun getCategories(): List<Category>

    fun setCategories(categories: List<Category>)

}