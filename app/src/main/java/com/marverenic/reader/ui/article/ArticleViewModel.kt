package com.marverenic.reader.ui.article

import android.content.Context
import android.content.Intent
import android.databinding.BaseObservable
import android.net.Uri
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import com.marverenic.reader.model.Article

class ArticleViewModel(
        private val context: Context,
        private val article: Article
) : BaseObservable() {

    val title: String
        get() = article.title.orEmpty()

    val source: String
        get() = article.origin?.title.orEmpty()

    val author: String
        get() = article.author.orEmpty()

    val content: Spanned
        get() = Html.fromHtml(article.summary?.content.orEmpty())

    val movementMethod = LinkMovementMethod.getInstance()!!

    fun openArticle() {
        val url = article.alternate?.first()?.href
        url?.let {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
        }
    }

}