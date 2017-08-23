package com.marverenic.reader.data

import com.marverenic.reader.data.service.FeedlyService
import com.marverenic.reader.model.Stream
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import retrofit2.Response
import java.io.IOException

private const val MAX_STREAM_ENTRIES = 1000

class FeedlyRssStore(private val authManager: AuthenticationManager,
                     private val service: FeedlyService) : RssStore {

    private val allArticlesStreamName: String
        get() = "user/${authManager.getFeedlyUserId()}/category/global.all"

    private val allArticles = RxLoader {
        service.getStream(authManager.getFeedlyAuthToken(),
                allArticlesStreamName, MAX_STREAM_ENTRIES)
                .unwrapResponse()
    }

    private val categories = RxLoader {
        service.getCategories(authManager.getFeedlyAuthToken()).unwrapResponse()
    }

    private val streams: MutableMap<String, RxLoader<Stream>> = mutableMapOf()

    override fun getAllArticles() = allArticles.getOrComputeValue()

    override fun getAllCategories() = categories.getOrComputeValue()

    override fun getStream(streamId: String): Single<Stream> {
        val loader = streams[streamId] ?: RxLoader {
            service.getStream(authManager.getFeedlyAuthToken(), streamId, MAX_STREAM_ENTRIES)
                    .unwrapResponse()
        }.also { streams[streamId] = it }

        return loader.getOrComputeValue()
    }

}

private fun <T: Any> Single<Response<T>>.unwrapResponse(): Single<T> =
        map {
            if (it.isSuccessful) it.body()
            else throw IOException("${it.code()}: ${it.errorBody()?.string()}")
        }

private class RxLoader<T>(val load: () -> Single<T>) {

    private var value: BehaviorSubject<T> = BehaviorSubject.create()

    val isLoaded: Boolean
        get() = value.hasValue()

    fun computeValue(): Single<T> {
        value = BehaviorSubject.create()
        load().subscribe(value::onNext)
        return value.firstOrError()
    }

    fun getOrComputeValue(): Single<T> {
        return if (!isLoaded) computeValue()
        else value.firstOrError()
    }

}