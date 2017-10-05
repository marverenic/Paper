package com.marverenic.reader.model

import android.os.Parcel
import android.os.Parcelable

data class Tag(
        val id: String,
        val label: String?
) : Parcelable {

    constructor(parcel: Parcel) : this(
            id = parcel.readString(),
            label = parcel.readString()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.apply {
            writeString(id)
            writeString(label)
        }
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Tag> {
        override fun createFromParcel(parcel: Parcel): Tag {
            return Tag(parcel)
        }

        override fun newArray(size: Int): Array<Tag?> {
            return arrayOfNulls(size)
        }
    }

}