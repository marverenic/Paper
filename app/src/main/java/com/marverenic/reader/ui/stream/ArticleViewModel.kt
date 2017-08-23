package com.marverenic.reader.ui.stream

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.view.View
import com.marverenic.reader.BR
import com.marverenic.reader.model.Article
import com.marverenic.reader.model.toDateString

class ArticleViewModel(private val context: Context, article: Article) : BaseObservable() {

    var article: Article = article
        set(value) {
            field = value
            notifyPropertyChanged(BR.title)
            notifyPropertyChanged(BR.description)
            notifyPropertyChanged(BR.date)
            notifyPropertyChanged(BR.source)
            notifyPropertyChanged(BR.sourceVisibility)
        }

    val title: String
        @Bindable get() = article.title.orEmpty()

    val description: String
        @Bindable get() = article.summary?.content.orEmpty()

    val date: String
        @Bindable get() = article.published.toDateString(context)

    val source: String?
        @Bindable get() = article.origin?.title

    val sourceVisibility: Int
        @Bindable get() = if (source == null) View.GONE else View.VISIBLE

}