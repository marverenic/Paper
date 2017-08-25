package com.marverenic.reader.data.service

const val ACTION_READ = "markAsRead"
const val ACTION_UNREAD = "keepUnread"
const val ACTION_SAVE = "markAsSaved"
const val ACTION_UNSAVE = "markAsUnsaved"

private val MARKER_ACTIONS = listOf(ACTION_READ, ACTION_UNREAD, ACTION_SAVE, ACTION_UNSAVE)

sealed class MarkerRequest(action: String) {

    abstract val type: String

    init {
        require(action in MARKER_ACTIONS) { "Invalid action: $action" }
    }
}

data class ArticleMarkerRequest(val entryIds: List<String>,
                                val action: String) : MarkerRequest(action) {

    override val type = "entries"

}

data class FeedMarkerRequest(val feedIds: List<String>,
                             val lastReadEntryId: String,
                             val action: String) : MarkerRequest(action) {

    override val type = "feeds"
}

data class CategoryMarkerRequest(val categoryIds: List<String>,
                                 val lastReadEntryId: String,
                                 val action: String) : MarkerRequest(action) {

    override val type = "categories"

}
