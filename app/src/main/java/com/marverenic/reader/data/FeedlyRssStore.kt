package com.marverenic.reader.data

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
                     private val service: FeedlyService) : RssStore {

    private val allArticlesStreamId: String
        get() = "user/${authManager.getFeedlyUserId()}/category/global.all"

    private val categories = RxLoader {
        service.getCategories(authManager.getFeedlyAuthToken()).unwrapResponse()
    }

    private val streams: MutableMap<String, RxLoader<Stream>> = mutableMapOf()

    override fun getAllArticles() = getStream(allArticlesStreamId)

    override fun getAllCategories() = categories.getOrComputeValue()

    private fun getStreamLoader(streamId: String): RxLoader<Stream> {
        return streams[streamId] ?: RxLoader {
            service.getStream(authManager.getFeedlyAuthToken(), streamId, STREAM_LOAD_SIZE)
                    .unwrapResponse()
        }.also { streams[streamId] = it }
    }

    override fun getStream(streamId: String) = getStreamLoader(streamId).getOrComputeValue()

    override fun refreshStream(streamId: String) {
        getStreamLoader(streamId).computeValue()
    }

    override fun isLoadingStream(streamId: String) = getStreamLoader(streamId).isComputingValue()

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

private class RxLoader<T>(val load: () -> Single<T>) {

    private var value: BehaviorSubject<T> = BehaviorSubject.create()

    private val isLoading: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    fun computeValue(): Observable<T> {
        load().subscribe(value::onNext)
        isLoading.take(1).subscribe { loading ->
            if (!loading) {
                isLoading.onNext(true)
                load().doOnEvent { _, _ -> isLoading.onNext(false) }
                        .subscribe(value::onNext)
            }
        }
        return value
    }

    fun getOrComputeValue(): Observable<T> {
        return if (!value.hasValue()) computeValue()
        else value
    }

    fun isComputingValue(): Observable<Boolean> = isLoading

    fun getValue(): T? = if (value.hasValue()) value.value else null

    fun setValue(t: T) {
        value.onNext(t)
    }

}