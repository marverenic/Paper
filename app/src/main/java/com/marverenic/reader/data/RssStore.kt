package com.marverenic.reader.data

import com.marverenic.reader.model.Article
import com.marverenic.reader.model.Category
import com.marverenic.reader.model.Stream
import io.reactivex.Single

interface RssStore {

    fun getAllArticles(): Single<Stream>

    fun getAllCategories(): Single<List<Category>>

    fun getStream(streamId: String): Single<Stream>

    fun markAsRead(article: Article, read: Boolean = true)

}