package com.marverenic.reader.ui.stream

import android.content.Context
import android.databinding.BaseObservable
import android.support.v7.widget.LinearLayoutManager
import com.marverenic.reader.model.Article
import com.marverenic.reader.ui.common.article.ArticleAdapter
import com.marverenic.reader.ui.common.article.ArticleReadCallback

class StreamViewModel(context: Context,
                      entries: List<Article> = emptyList(),
                      val callback: ArticleReadCallback) : BaseObservable() {

    var entries: List<Article> = entries
        set(value) {
            field = value
            adapter.articles = value
        }

    val adapter: ArticleAdapter by lazy(LazyThreadSafetyMode.NONE) {
        ArticleAdapter(entries, callback)
    }

    val layoutManager: LinearLayoutManager = LinearLayoutManager(context)

}