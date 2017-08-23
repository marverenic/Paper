package com.marverenic.reader.model

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