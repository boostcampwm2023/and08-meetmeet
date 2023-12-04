package com.teameetmeet.meetmeet.data.model

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EventMember(
    @Json(name = "id")
    val id: Int,
    @Json(name = "nickname")
    val nickname: String,
    @Json(name = "profile")
    val profile: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString()
    )

    override fun describeContents(): Int {
        return id
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nickname)
        parcel.writeString(profile)
    }

    companion object CREATOR : Parcelable.Creator<EventMember> {
        override fun createFromParcel(parcel: Parcel): EventMember {
            return EventMember(parcel)
        }

        override fun newArray(size: Int): Array<EventMember?> {
            return arrayOfNulls(size)
        }
    }
}
