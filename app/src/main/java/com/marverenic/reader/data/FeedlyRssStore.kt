package com.marverenic.reader.data

import android.util.Log
import com.marverenic.reader.data.database.RssDatabase
import com.marverenic.reader.data.service.ACTION_READ
import com.marverenic.reader.data.service.ArticleMarkerRequest
import com.marverenic.reader.data.service.FeedlyService
import com.marverenic.reader.model.Article
import com.marverenic.reader.model.Stream
import com.marverenic.reader.utils.replaceAll
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
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

        return RxLoader(cachedStream) {
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

private inline fun <T: Any> loadAsync(crossinline load: () -> T?): Maybe<T> {
    return Observable.fromCallable {
                return@fromCallable listOf(load()).filterNotNull()
            }
            .flatMap { Observable.fromIterable(it) }
            .firstElement()
            .subscribeOn(Schedulers.io())
}

private class RxLoader<T>(default: Maybe<T>? = null, val load: () -> Single<T>) {

    private val subject: BehaviorSubject<T> = BehaviorSubject.create()

    private val isLoading: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    private var workerDisposable: Disposable? = null

    init {
        default?.let {
            isLoading.onNext(true)
            workerDisposable = it.subscribe(this::setValue) { t ->
                Log.e("RxLoader", "Failed to load default value", t)
                computeValue()
            }
        }
    }

    fun computeValue(): Observable<T> {
        isLoading.take(1).subscribe { loading ->
            if (!loading) {
                isLoading.onNext(true)
                workerDisposable = load()
                        .doOnEvent { _, _ -> isLoading.onNext(false) }
                        .subscribe(this::setValue)
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
        workerDisposable?.dispose()
        workerDisposable = null

        subject.onNext(t)
        isLoading.onNext(false)
    }

    fun getObservable(): Observable<T> = subject

}