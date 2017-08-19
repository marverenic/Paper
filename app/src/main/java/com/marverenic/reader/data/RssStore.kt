package com.marverenic.reader.data

import com.marverenic.reader.model.Article
import com.marverenic.reader.model.Category
import io.reactivex.Single

interface RssStore {

    fun getAllArticles(): Single<List<Article>>

    fun getAllCategories(): Single<List<Category>>

    fun getArticlesInCategory(): Single<List<Article>>

}