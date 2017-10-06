package com.marverenic.reader.ui.article

import android.content.Context
import android.databinding.BaseObservable
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.text.format.DateUtils.*
import com.marverenic.reader.R
import com.marverenic.reader.model.Article
import com.marverenic.reader.utils.resolveIntAttr

class ArticleViewerViewModel(
        private val context: Context,
        private val article: Article
) : BaseObservable() {

    val title: String
        get() = article.title.orEmpty()

    val source: String
        get() = article.origin?.title.orEmpty()

    val author: String
        get() = article.author.orEmpty()

    val timestamp: String
        get() = formatDateTime(context, article.timestamp,
                FORMAT_SHOW_TIME or FORMAT_SHOW_DATE or FORMAT_SHOW_WEEKDAY)

    val summary = article.content ?: article.summary

    val movementMethod = ArticleLinkMovementMethod

    fun openArticle() {
        val url = article.alternate?.first()?.href
        url?.let {
            val intent = CustomTabsIntent.Builder()
                    .addDefaultShareMenuItem()
                    .setToolbarColor(context.resolveIntAttr(R.attr.colorPrimary))
                    .enableUrlBarHiding()
                    .build()

            intent.launchUrl(context, Uri.parse(it))
        }
    }

}