package com.marverenic.reader.model

import android.os.Parcel
import android.os.Parcelable

private val HTML_TAG_REGEX = Regex("""<[^>]*>""")

data class Content(val content: String) : Parcelable {

    @Transient private var plaintextContent: String? = null
    @Transient private var summary: String? = null

    constructor(parcel: Parcel) : this(parcel.readString())

    fun asPlaintext(): String {
        if (plaintextContent == null) {
            plaintextContent = content.replace(HTML_TAG_REGEX, "").trim()
        }

        return plaintextContent!!
    }

    fun summary(): String {
        if (summary == null) {
            summary = asPlaintext().substringBefore("\n")
        }

        return summary!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.apply {
            writeString(content)
        }
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Content> {
        override fun createFromParcel(parcel: Parcel): Content {
            return Content(parcel)
        }

        override fun newArray(size: Int): Array<Content?> {
            return arrayOfNulls(size)
        }
    }

}