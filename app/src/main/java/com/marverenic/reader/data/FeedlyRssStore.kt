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
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import retrofit2.Response
import java.io.IOException

private const val STREAM_LOAD_SIZE = 250
private const val MAX_STREAM_CACHE_AGE: Seconds = 24 * 60 * 60 // 1 day

class FeedlyRssStore(private val authManager: AuthenticationManager,
                     private val service: FeedlyService,
                     private val rssDatabase: RssDatabase) : RssStore {

    private val allArticlesStreamId: Single<String>
        get() = authManager.getFeedlyUserId().map { "user/$it/category/global.all" }

    private val categories = RxLoader(loadAsync { rssDatabase.getCategories() }) {
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

    private val streams: MutableMap<String, RxLoader<Stream>> = mutableMapOf()

    override fun getAllArticles(): Observable<Stream> = allArticlesStreamId.flatMapObservable { getStream(it) }

    override fun getAllCategories() = categories.getOrComputeValue()

    private fun getStreamLoader(streamId: String): RxLoader<Stream> {
        streams[streamId]?.let { return it }

        var cached = false
        val cachedStream = loadAsync {
            val stream = rssDatabase.getStream(streamId)
            cached = stream != null
            return@loadAsync stream
        }

        val isCacheStale = loadAsync {
            val cacheAge = rssDatabase.getStreamTimestamp(streamId).orEpoch()
            val expirationDate = cacheAge + MAX_STREAM_CACHE_AGE.toDuration()
            return@loadAsync DateTime.now() > expirationDate
        }

        return RxLoader(cachedStream, isCacheStale) {
            authManager.getFeedlyAuthToken()
                    .flatMap { service.getStream(it, streamId, STREAM_LOAD_SIZE) }
                    .unwrapResponse()
                    .map { it.copy(items = it.items.sortedByDescending(Article::timestamp)) }
        }.also { loader ->
            streams[streamId] = loader
            loader.getObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .skipWhile { cached.also { cached = false } }
                    .subscribe { stream -> rssDatabase.insertStream(stream) }
        }
    }

    override fun getStream(streamId: String) = getStreamLoader(streamId).getOrComputeValue()

    override fun refreshStream(streamId: String) {
        getStreamLoader(streamId).computeValue()
    }

    override fun refreshAllArticles() {
        allArticlesStreamId.subscribe { streamId -> refreshStream(streamId) }
    }

    override fun refreshCategories() {
        categories.computeValue()
    }

    override fun isLoadingStream(streamId: String) = getStreamLoader(streamId).isComputingValue()

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
