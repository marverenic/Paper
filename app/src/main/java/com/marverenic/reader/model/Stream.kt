package com.marverenic.reader.model

data class Stream(
        val id: String,
        val continuation: String?,
        val title: String?,
        val items: List<Article>
)