package com.marverenic.reader.model

import android.os.Parcel
import android.os.Parcelable

data class Visual(
        val width: Int,
        val height: Int,
        val url: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
            width = parcel.readInt(),
            height = parcel.readInt(),
            url = parcel.readString()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.apply {
            writeInt(width)
            writeInt(height)
            writeString(url)
        }
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Visual> {
        override fun createFromParcel(parcel: Parcel): Visual {
            return Visual(parcel)
        }

        override fun newArray(size: Int): Array<Visual?> {
            return arrayOfNulls(size)
        }
    }

}