package com.marverenic.reader.ui.stream

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import com.marverenic.reader.BR
import com.marverenic.reader.model.Article

class ArticleViewModel(private val context: Context, article: Article) : BaseObservable() {

    var article: Article = article
        set(value) {
            field = value
            notifyPropertyChanged(BR.title)
        }

    val title: String
        @Bindable get() = article.title.orEmpty()

    /*val description: String
        get() = article.*/

}