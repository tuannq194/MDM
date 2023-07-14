package com.ngxqt.mdm.data.model.objectmodel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notification(
    @SerializedName("id") val id: Int?,
    @SerializedName("content") val content: String?,
    @SerializedName("user_id") val userId: Int?,
    @SerializedName("is_seen") val isSeen: Int?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?,
    @SerializedName("User") val notificationUser: NotificationUser?
) : Parcelable

@Parcelize
data class NotificationUser(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?
) : Parcelable
