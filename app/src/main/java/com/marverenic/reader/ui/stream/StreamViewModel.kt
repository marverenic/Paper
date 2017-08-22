package com.marverenic.reader.ui.stream

import android.content.Context
import android.databinding.BaseObservable
import android.support.v7.widget.LinearLayoutManager
import com.marverenic.reader.model.Article

class StreamViewModel(context: Context,
                      entries: List<Article> = emptyList()) : BaseObservable() {

    var entries: List<Article> = entries
        set(value) {
            field = value
            adapter.articles = value
        }

    val adapter: ArticleAdapter by lazy(LazyThreadSafetyMode.NONE) {
        ArticleAdapter(entries)
    }

    val layoutManager: LinearLayoutManager = LinearLayoutManager(context)

}