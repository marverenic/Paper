package com.marverenic.reader.model

import android.os.Build
import android.text.Html

data class Article(
        val id: String,
        val title: String?,
        val author: String?,
        val published: Timestamp,
        val updated: Timestamp?,
        val unread: Boolean,
        val origin: Origin?,
        val visual: Visual?,
        val summary: Content?,
        val alternate: List<Link>?,
        val tags: List<Tag>?,
        val keywords: List<String>?
)

data class Visual(
        val width: Int,
        val height: Int,
        val url: String
)

private const val OBJECT_REPLACEMENT_CHARACTER = (0xFFFC).toChar()

data class Content(val content: String) {

    @Transient private var plaintextContent: String? = null

    fun asPlaintext(): String {
        if (plaintextContent == null) {
            plaintextContent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(content, 0)
            } else {
                @Suppress("DEPRECATION")
                Html.fromHtml(content)
            }.toString()
                    .filter { it != OBJECT_REPLACEMENT_CHARACTER }
                    .trim()
                    .substringBefore("\n")
        }

        return plaintextContent!!
    }

}

data class Link(val href: String, val type: String)

data class Origin(val htmlUrl: String, val title: String, val streamId: String)