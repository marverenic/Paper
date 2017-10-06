package com.marverenic.reader.utils.bindingadapters

import android.databinding.BindingAdapter
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.widget.TextView
import com.marverenic.reader.model.Content
import com.marverenic.reader.ui.article.PicassoImageGetter
import com.marverenic.reader.utils.whenMeasured

@BindingAdapter("content")
fun bindContent(view: TextView, content: Content?) {
    view.whenMeasured {
        text = parseHtml(
                html = content?.content.orEmpty(),
                imageGetter = PicassoImageGetter(
                        view.context,
                        maxWidth = view.measuredWidth - view.paddingStart - view.paddingEnd
                ) { text = text }
        )
    }
}

private fun parseHtml(html: String,
                      imageGetter: Html.ImageGetter? = null,
                      tagHandler: Html.TagHandler? = null): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY, imageGetter, tagHandler)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(html, imageGetter, tagHandler)
    }
}
