package com.marverenic.reader.data

import com.marverenic.reader.data.database.RssDatabase
import com.marverenic.reader.data.service.ACTION_READ
import com.marverenic.reader.data.service.ArticleMarkerRequest
import com.marverenic.reader.data.service.FeedlyService
import com.marverenic.reader.model.Article
import com.marverenic.reader.model.Seconds
import com.marverenic.reader.model.Stream
import com.marverenic.reader.model.toDuration
import com.marverenic.reader.utils.orEpoch
import com.marverenic.reader.utils.replaceAll
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import retrofit2.Response
import java.io.IOException

private const val STREAM_LOAD_SIZE = 250
private const val MAX_STREAM_CACHE_AGE: Seconds = 24 * 60 * 60 // 1 day
private const val MAX_CATEGORY_CACHE_AGE: Seconds = 7 * 24 * 60 * 60 // 1 week

class FeedlyRssStore(private val authManager: AuthenticationManager,
                     private val service: FeedlyService,
                     private val prefStore: PreferenceStore,
                     private val rssDatabase: RssDatabase) : RssStore {

    private val allArticlesStreamId: Single<String>
        get() = authManager.getFeedlyUserId().map { "user/$it/category/global.all" }

    private val categories = RxLoader(loadAsync { rssDatabase.getCategories() },
            isCacheStale(MAX_CATEGORY_CACHE_AGE) { prefStore.lastCategoryRefreshTime }) {
        authManager.getFeedlyAuthToken()
                .flatMap { service.getCategories(it) }
                .unwrapResponse()
                .also {
                    it.subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .subscribe { categories ->
                                rssDatabase.setCategories(categories)
                            }
                }
    }

    private val streams = mutableMapOf<String, RxLoader<Stream>>()
    private val unreadStreams = mutableMapOf<String, RxLoader<Stream>>()

    override fun getAllArticles(unreadOnly: Boolean): Observable<Stream> {
        return allArticlesStreamId.flatMapObservable { getStream(it, unreadOnly) }
    }

    override fun getAllCategories() = categories.getOrComputeValue()

    private fun getStreamLoader(streamId: String, unreadOnly: Boolean): RxLoader<Stream> {
        val streamsMap = if (unreadOnly) unreadStreams else streams
        streamsMap[streamId]?.let { return it }

        var cached = false
        val cachedStream = loadAsync {
            val stream = rssDatabase.getStream(streamId)
            cached = stream != null
            return@loadAsync stream
        }

        val stale = isCacheStale(MAX_STREAM_CACHE_AGE) { rssDatabase.getStreamTimestamp(streamId) }

        return RxLoader(cachedStream, stale) {
            authManager.getFeedlyAuthToken()
                    .flatMap { service.getStream(it, streamId, STREAM_LOAD_SIZE, unreadOnly) }
                    .unwrapResponse()
                    .map { it.copy(items = it.items.sortedByDescending(Article::timestamp)) }
        }.also { loader ->
            streamsMap[streamId] = loader
            loader.getObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .skipWhile { cached.also { cached = false } }
                    .subscribe { stream -> rssDatabase.insertStream(stream) }
        }
    }

    override fun getStream(streamId: String, unreadOnly: Boolean): Observable<Stream> {
        return getStreamLoader(streamId, unreadOnly).getOrComputeValue()
    }

    override fun refreshStream(streamId: String) {
        getStreamLoader(streamId, true).computeValue()
        getStreamLoader(streamId, false).computeValue()
    }

    override fun refreshAllArticles() {
        allArticlesStreamId.subscribe { streamId -> refreshStream(streamId) }
    }

    override fun refreshCategories() {
        categories.computeValue()
    }

    override fun isLoadingStream(streamId: String): Observable<Boolean> {
        val loadingRead = getStreamLoader(streamId, false).isComputingValue()
        val loadingUnread = getStreamLoader(streamId, true).isComputingValue()

        return Observable.combineLatest(loadingRead, loadingUnread,
                BiFunction { readLoading, unreadLoading -> readLoading || unreadLoading })
    }

    override fun loadMoreArticles(stream: Stream) {
        if (stream.continuation == null) {
            return
        }

        streams[stream.id]?.let { loader ->
            authManager.getFeedlyAuthToken()
                    .flatMap { service.getStreamContinuation(it, stream.id, stream.continuation, STREAM_LOAD_SIZE) }
                    .unwrapResponse()
                    .subscribe { continuation ->
                        val combinedArticles = stream.items + continuation.items
                        val merged = continuation.copy(items = combinedArticles.sortedByDescending(Article::timestamp))
                        loader.setValue(merged)
                    }
        }
    }

    override fun markAsRead(article: Article, read: Boolean) {
        if (article.unread == !read) {
            return
        }

        authManager.getFeedlyAuthToken()
                .flatMap {
                    service.markArticles(it, ArticleMarkerRequest(
                            entryIds = listOf(article.id),
                            action = ACTION_READ))
                }
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

private inline fun <T: Any> loadAsync(crossinline load: () -> T?): Single<T> {
    return Single.fromCallable { load() ?: throw NoSuchElementException("No value returned") }
            .subscribeOn(Schedulers.io())
}

private inline fun isCacheStale(maxAge: Seconds, crossinline age: () -> DateTime?): Single<Boolean> {
    return Single.fromCallable {
        val expirationDate = age().orEpoch() + maxAge.toDuration()
        return@fromCallable DateTime.now() > expirationDate
    }
}
