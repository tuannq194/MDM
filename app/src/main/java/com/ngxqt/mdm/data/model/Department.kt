package com.ngxqt.mdm.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Department(
    @SerializedName("id") val id: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("code") val code: String?,
    @SerializedName("slug") val slug: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("contact") val contact: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("user_id") val user_id: Int?,
    @SerializedName("author_id") val author_id: Int?,
    @SerializedName("nursing_id") val nursing_id: Int?,
    @SerializedName("image") val image: String?,
    @SerializedName("created_at") val created_at: String?,
    @SerializedName("updated_at") val updated_at: String?,
    @SerializedName("browser") val browser: String?,
    @SerializedName("browser_day") val browser_day: String?
): Parcelable
