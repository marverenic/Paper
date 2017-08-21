package com.marverenic.reader.data

import com.marverenic.reader.data.service.FeedlyService
import com.marverenic.reader.model.Article
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import retrofit2.Response
import java.io.IOException

class FeedlyRssStore(private val authManager: AuthenticationManager,
                     private val service: FeedlyService) : RssStore {

    private val categories = RxLoader {
        service.getCategories(authManager.getFeedlyAuthToken()).unwrapResponse()
    }

    override fun getAllArticles(): Single<List<Article>> {
        TODO("not implemented")
    }

    override fun getAllCategories() = categories.getOrComputeValue()

    override fun getArticlesInCategory(): Single<List<Article>> {
        TODO("not implemented")
    }

}

private fun <T: Any> Single<Response<T>>.unwrapResponse(): Single<T> =
        map {
            if (it.isSuccessful) it.body()
            else throw IOException("${it.errorBody()}")
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