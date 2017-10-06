package com.marverenic.reader.model

import android.os.Parcel
import android.os.Parcelable

data class Link(
        val href: String,
        val type: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
            href = parcel.readString(),
            type = parcel.readString()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.apply {
            writeString(href)
            writeString(type)
        }
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Link> {
        override fun createFromParcel(parcel: Parcel): Link {
            return Link(parcel)
        }

        override fun newArray(size: Int): Array<Link?> {
            return arrayOfNulls(size)
        }
    }

}