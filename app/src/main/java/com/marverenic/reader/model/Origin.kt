package com.marverenic.reader.model

import android.os.Parcel
import android.os.Parcelable

data class Origin(
        val htmlUrl: String,
        val title: String,
        val streamId: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
            htmlUrl = parcel.readString(),
            title = parcel.readString(),
            streamId = parcel.readString()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.apply {
            writeString(htmlUrl)
            writeString(title)
            writeString(streamId)
        }
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Origin> {
        override fun createFromParcel(parcel: Parcel): Origin {
            return Origin(parcel)
        }

        override fun newArray(size: Int): Array<Origin?> {
            return arrayOfNulls(size)
        }
    }

}