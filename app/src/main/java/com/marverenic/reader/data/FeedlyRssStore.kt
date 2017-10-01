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
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import retrofit2.Response
import java.io.IOException

private const val STREAM_LOAD_SIZE = 250

class FeedlyRssStore(private val authManager: AuthenticationManager,
                     private val service: FeedlyService,
                     private val rssDatabase: RssDatabase) : RssStore {

    private val allArticlesStreamId: Single<String>
        get() = authManager.getFeedlyUserId().map { "user/$it/category/global.all" }

    private val categories = RxLoader {
        authManager.getFeedlyAuthToken().flatMap { service.getCategories(it) }.unwrapResponse()
    }

    private val streams: MutableMap<String, RxLoader<Stream>> = mutableMapOf()

    override fun getAllArticles(): Observable<Stream> = allArticlesStreamId.flatMapObservable { getStream(it) }

    override fun getAllCategories() = categories.getOrComputeValue()

    private fun getStreamLoader(streamId: String): RxLoader<Stream> {
        val cached = rssDatabase.getStream(streamId)
        return streams[streamId] ?: RxLoader(cached) {
            authManager.getFeedlyAuthToken()
                    .flatMap { service.getStream(it, streamId, STREAM_LOAD_SIZE) }
                    .unwrapResponse()
        }.also { loader ->
            streams[streamId] = loader
            loader.getObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .skip(if (cached != null) 1 else 0)
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
                        val merged = continuation.copy(items = stream.items + continuation.items)
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

private class RxLoader<T>(default: T? = null, val load: () -> Single<T>) {

    private val subject: BehaviorSubject<T> = default?.let { BehaviorSubject.createDefault(it) }
            ?: BehaviorSubject.create()

    private val isLoading: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    fun computeValue(): Observable<T> {
        load().subscribe(subject::onNext)
        isLoading.take(1).subscribe { loading ->
            if (!loading) {
                isLoading.onNext(true)
                load().doOnEvent { _, _ -> isLoading.onNext(false) }
                        .subscribe(subject::onNext)
            }
        }
        return subject
    }

    fun getOrComputeValue(): Observable<T> {
        return if (!subject.hasValue()) computeValue()
        else subject
    }

    fun isComputingValue(): Observable<Boolean> = isLoading

    fun getValue(): T? = if (subject.hasValue()) subject.value else null

    fun setValue(t: T) {
        subject.onNext(t)
    }

    fun getObservable(): Observable<T> = subject

}