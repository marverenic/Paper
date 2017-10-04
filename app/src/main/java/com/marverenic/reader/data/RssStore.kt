package com.marverenic.reader.data

import com.marverenic.reader.model.Article
import com.marverenic.reader.model.Category
import com.marverenic.reader.model.Stream
import io.reactivex.Observable

interface RssStore {

    fun getAllArticles(unreadOnly: Boolean): Observable<Stream>

    fun getAllCategories(): Observable<List<Category>>

    fun getStream(streamId: String, unreadOnly: Boolean): Observable<Stream>

    fun refreshStream(streamId: String)

    fun refreshAllArticles()

    fun refreshCategories()

    fun isLoadingStream(streamId: String): Observable<Boolean>

    fun loadMoreArticles(stream: Stream)

    fun markAsRead(article: Article, read: Boolean = true)

}