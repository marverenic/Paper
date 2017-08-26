package com.marverenic.reader.data

import com.marverenic.reader.model.Article
import com.marverenic.reader.model.Category
import com.marverenic.reader.model.Stream
import io.reactivex.Observable

interface RssStore {

    fun getAllArticles(): Observable<Stream>

    fun getAllCategories(): Observable<List<Category>>

    fun getStream(streamId: String): Observable<Stream>

    fun loadMoreArticles(stream: Stream)

    fun markAsRead(article: Article, read: Boolean = true)

}