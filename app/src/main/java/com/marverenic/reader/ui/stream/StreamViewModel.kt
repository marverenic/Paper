package com.marverenic.reader.ui.stream

import android.content.Context
import android.databinding.BaseObservable
import android.support.v7.widget.LinearLayoutManager
import com.marverenic.reader.model.Stream
import com.marverenic.reader.ui.common.article.ArticleAdapter
import com.marverenic.reader.ui.common.article.ArticleFetchCallback
import com.marverenic.reader.ui.common.article.ArticleReadCallback

class StreamViewModel(context: Context,
                      stream: Stream? = null,
                      val readCallback: ArticleReadCallback,
                      val fetchCallback: ArticleFetchCallback)
    : BaseObservable() {

    var entries: Stream? = stream
        set(value) {
            field = value
            adapter.stream = value
        }

    val adapter: ArticleAdapter by lazy(LazyThreadSafetyMode.NONE) {
        ArticleAdapter(entries, readCallback, fetchCallback)
    }

    val layoutManager: LinearLayoutManager = LinearLayoutManager(context)

}