package com.marverenic.reader.ui.home.all

import android.content.Context
import android.databinding.BaseObservable
import android.support.v7.widget.LinearLayoutManager
import com.marverenic.reader.model.Stream
import com.marverenic.reader.ui.common.article.ArticleAdapter
import com.marverenic.reader.ui.common.article.ArticleFetchCallback
import com.marverenic.reader.ui.common.article.ArticleReadCallback

class AllArticlesViewModel(val context: Context,
                           stream: Stream? = null,
                           val readCallback: ArticleReadCallback,
                           val fetchCallback: ArticleFetchCallback)
    : BaseObservable() {

    var stream: Stream? = stream
        set(value) {
            field = value
            adapter.stream = stream
            adapter.notifyDataSetChanged()
        }

    val adapter: ArticleAdapter by lazy(LazyThreadSafetyMode.NONE) {
        ArticleAdapter(stream, readCallback, fetchCallback)
    }

    val layoutManager = LinearLayoutManager(context)

}