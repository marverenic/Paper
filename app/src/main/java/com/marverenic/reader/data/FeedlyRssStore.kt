package com.marverenic.reader.data

import com.marverenic.reader.data.service.FeedlyService
import com.marverenic.reader.model.Article
import com.marverenic.reader.model.Category
import io.reactivex.Single
import java.io.IOException

class FeedlyRssStore(private val authManager: AuthenticationManager,
                     private val service: FeedlyService) : RssStore {

    override fun getAllArticles(): Single<List<Article>> {
        TODO("not implemented")
    }

    override fun getAllCategories(): Single<List<Category>> =
            service.getCategories(authManager.getFeedlyAuthToken())
                    .map {
                        if (it.isSuccessful) it.body()
                        else throw IOException("${it.errorBody()}")
                    }

    override fun getArticlesInCategory(): Single<List<Article>> {
        TODO("not implemented")
    }

}