package com.marverenic.reader.ui.home.all

import android.content.Context
import android.databinding.BaseObservable
import android.support.v7.widget.LinearLayoutManager
import com.marverenic.reader.model.Article
import com.marverenic.reader.ui.common.article.ArticleAdapter
import com.marverenic.reader.ui.common.article.ArticleReadCallback

class AllArticlesViewModel(val context: Context,
                           articles: List<Article> = emptyList(),
                           val callback: ArticleReadCallback) : BaseObservable() {

    var articles: List<Article> = articles
        set(value) {
            field = value
            adapter.articles = value
            adapter.notifyDataSetChanged()
        }

    val adapter: ArticleAdapter by lazy(LazyThreadSafetyMode.NONE) {
        ArticleAdapter(articles, callback)
    }

    val layoutManager = LinearLayoutManager(context)

}