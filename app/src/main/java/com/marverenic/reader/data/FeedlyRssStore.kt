package com.marverenic.reader.data

import com.marverenic.reader.data.service.FeedlyService
import com.marverenic.reader.model.Article
import com.marverenic.reader.model.Category
import io.reactivex.Single

class FeedlyRssStore(private val service: FeedlyService) : RssStore {

    override fun getAllArticles(): Single<List<Article>> {
        TODO("not implemented")
    }

    override fun getAllCategories(): Single<List<Category>> {
        TODO("not implemented")
    }

    override fun getArticlesInCategory(): Single<List<Article>> {
        TODO("not implemented")
    }

}