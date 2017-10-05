package com.marverenic.reader.ui.common.article

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.view.View
import com.marverenic.reader.BR
import com.marverenic.reader.model.Article
import com.marverenic.reader.model.toDateString
import com.marverenic.reader.ui.article.ArticleActivity

private const val UNREAD_ALPHA = 1.0f
private const val READ_ALPHA = 0.5f

class ArticleViewModel(private val context: Context,
                       private val callback: ArticleReadCallback,
                       article: Article)
    : BaseObservable() {

    var article: Article = article
        set(value) {
            field = value
            notifyPropertyChanged(BR.title)
            notifyPropertyChanged(BR.description)
            notifyPropertyChanged(BR.date)
            notifyPropertyChanged(BR.source)
            notifyPropertyChanged(BR.sourceVisibility)
            notifyPropertyChanged(BR.textOpacity)
        }

    val title: String
        @Bindable get() = article.title.orEmpty()

    val description: String
        @Bindable get() = article.summary?.summary().orEmpty()

    val date: String
        @Bindable get() = article.timestamp.toDateString(context)

    val source: String?
        @Bindable get() = article.origin?.title

    val sourceVisibility: Int
        @Bindable get() = if (source == null) View.GONE else View.VISIBLE

    val textOpacity: Float
        @Bindable get() = if (article.unread) UNREAD_ALPHA else READ_ALPHA

    fun onClickArticle() {
        context.startActivity(ArticleActivity.newIntent(context, article))
        callback(article)
    }

}