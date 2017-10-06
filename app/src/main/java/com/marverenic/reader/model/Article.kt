package com.marverenic.reader.model

import android.os.Parcel
import android.os.Parcelable
import com.marverenic.reader.utils.*

data class Article(
        val id: String,
        val title: String?,
        val author: String?,
        val published: Timestamp,
        val updated: Timestamp?,
        val unread: Boolean,
        val origin: Origin?,
        val visual: Visual?,
        val content: Content?,
        val summary: Content?,
        val alternate: List<Link>?,
        val tags: List<Tag>?,
        val keywords: List<String>?
) : Parcelable {

    val timestamp: Timestamp
        get() = Math.max(published, updated ?: 0)

    constructor(parcel: Parcel) : this(
            id = parcel.readString(),
            title = parcel.readString(),
            author = parcel.readString(),
            published = parcel.readLong(),
            updated = parcel.readOptionalLong(),
            unread = parcel.readBoolean(),
            origin = parcel.readParcelable(),
            visual = parcel.readParcelable(),
            content = parcel.readParcelable(),
            summary = parcel.readParcelable(),
            alternate = parcel.readList(),
            tags = parcel.readList(),
            keywords = parcel.readList()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.apply {
            writeString(id)
            writeString(title)
            writeString(author)
            writeLong(published)
            writeOptionalLong(updated)
            writeBoolean(unread)
            writeParcelable(origin, 0)
            writeParcelable(visual, 0)
            writeParcelable(content, 0)
            writeParcelable(summary, 0)
            writeList(alternate)
            writeList(tags)
            writeList(keywords)
        }
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Article> {
        override fun createFromParcel(parcel: Parcel): Article {
            return Article(parcel)
        }

        override fun newArray(size: Int): Array<Article?> {
            return arrayOfNulls(size)
        }
    }

}