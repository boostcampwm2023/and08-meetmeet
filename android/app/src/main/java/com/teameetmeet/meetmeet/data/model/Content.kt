package com.teameetmeet.meetmeet.data.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Content(
    @Json(name = "id")
    val id: Int,
    @Json(name = "mimeType")
    val mimeType: String,
    @Json(name = "path")
    val path: String
) : Parcelable