package com.marverenic.reader.model

data class Article(
        val id: String,
        val title: String?,
        val author: String?,
        val published: Timestamp,
        val updated: Timestamp?,
        val unread: Boolean,
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

data class Content(val content: String)

data class Link(val href: String, val type: String)