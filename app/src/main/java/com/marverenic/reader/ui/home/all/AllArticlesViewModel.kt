package com.marverenic.reader.ui.home.all

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import com.marverenic.reader.BR
import com.marverenic.reader.R
import com.marverenic.reader.model.Stream
import com.marverenic.reader.ui.common.article.ArticleAdapter
import com.marverenic.reader.ui.common.article.ArticleFetchCallback
import com.marverenic.reader.ui.common.article.ArticleReadCallback
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class AllArticlesViewModel(val context: Context,
                           stream: Stream? = null,
                           var streamTitle: String,
                           val readCallback: ArticleReadCallback,
                           val fetchCallback: ArticleFetchCallback)
    : BaseObservable() {

    var stream: Stream? = stream
        set(value) {
            field = value
            adapter.stream = stream
            adapter.notifyDataSetChanged()
        }

    var refreshing: Boolean = (stream == null)
        set(value) {
            if (value != field) {
                refreshSubject.onNext(value)
                field = value
                notifyPropertyChanged(BR.refreshing)
            }
        }
        @Bindable get() = field

    private val refreshSubject = BehaviorSubject.createDefault(refreshing)

    val adapter: ArticleAdapter by lazy(LazyThreadSafetyMode.NONE) {
        ArticleAdapter(stream, streamTitle, readCallback, fetchCallback)
    }

    val layoutManager = LinearLayoutManager(context)

    val swipeRefreshColors = intArrayOf(
            ContextCompat.getColor(context, R.color.colorPrimary),
            ContextCompat.getColor(context, R.color.colorPrimaryDark),
            ContextCompat.getColor(context, R.color.colorAccent)
    )

    fun getRefreshObservable(): Observable<Boolean> = refreshSubject

}