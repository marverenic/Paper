package com.marverenic.reader.data

import com.marverenic.reader.data.database.RssDatabase
import com.marverenic.reader.data.service.ACTION_READ
import com.marverenic.reader.data.service.ArticleMarkerRequest
import com.marverenic.reader.data.service.FeedlyService
import com.marverenic.reader.model.Article
import com.marverenic.reader.model.Stream
import com.marverenic.reader.utils.replaceAll
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import retrofit2.Response
import java.io.IOException

private const val STREAM_LOAD_SIZE = 250

class FeedlyRssStore(private val authManager: AuthenticationManager,
                     private val service: FeedlyService,
                     private val rssDatabase: RssDatabase) : RssStore {

    private val allArticlesStreamId: String
        get() = "user/${authManager.getFeedlyUserId()}/category/global.all"

    private val categories = RxLoader {
        service.getCategories(authManager.getFeedlyAuthToken()).unwrapResponse()
    }

    private val streams: MutableMap<String, RxLoader<Stream>> = mutableMapOf()

    override fun getAllArticles() = getStream(allArticlesStreamId)

    override fun getAllCategories() = categories.getOrComputeValue()

    override fun getStream(streamId: String): Observable<Stream> {
        val loader = streams[streamId] ?: RxLoader(rssDatabase.getStream(streamId)) {
            service.getStream(authManager.getFeedlyAuthToken(), streamId, STREAM_LOAD_SIZE)
                    .unwrapResponse()
                    .doOnSuccess { rssDatabase.insertStream(it) }
        }.also { streams[streamId] = it }

        return loader.getOrComputeValue()
    }

    override fun loadMoreArticles(stream: Stream) {
        if (stream.continuation == null) {
            return
        }

        streams[stream.id]?.let { loader ->
            service
                    .getStreamContinuation(authManager.getFeedlyAuthToken(), stream.id,
                            stream.continuation, STREAM_LOAD_SIZE)
                    .unwrapResponse()
                    .subscribe { continuation ->
                        val merged = continuation.copy(items = stream.items + continuation.items)
                        loader.setValue(merged)
                    }
        }
    }

    override fun markAsRead(article: Article, read: Boolean) {
        if (article.unread == !read) {
            return
        }

        service
                .markArticles(authManager.getFeedlyAuthToken(), ArticleMarkerRequest(
                        entryIds = listOf(article.id), action = ACTION_READ))
                .unwrapResponse()
                .subscribe()

        val readArticle = article.copy(unread = !read)
        streams.forEach { (_, loader) ->
            loader.getValue()?.let { stream ->
                val newContents = stream.items.replaceAll(article, readArticle)
                loader.setValue(stream.copy(items = newContents))
            }
        }
    }

}

private fun <T: Any> Single<Response<T>>.unwrapResponse(): Single<T> =
        map {
            if (it.isSuccessful) it.body()
            else throw IOException("${it.code()}: ${it.errorBody()?.string()}")
        }

private class RxLoader<T>(default: T? = null, val load: () -> Single<T>) {

    private val value: BehaviorSubject<T> = default?.let { BehaviorSubject.createDefault(it) }
            ?: BehaviorSubject.create()

    val isLoaded: Boolean
        get() = value.hasValue()

    fun computeValue(): Observable<T> {
        load().subscribe(value::onNext)
        return value
    }

    fun getOrComputeValue(): Observable<T> {
        return if (!isLoaded) computeValue()
        else value
    }

    fun getValue(): T? = if (value.hasValue()) value.value else null

    fun setValue(t: T) {
        value.onNext(t)
    }

}