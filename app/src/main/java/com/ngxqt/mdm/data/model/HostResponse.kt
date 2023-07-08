package com.ngxqt.mdm.data.model

import com.google.gson.annotations.SerializedName

data class HostResponse(
    @SerializedName("success")
    val success: Boolean?,
    @SerializedName("data")
    val data: Data?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("code")
    val code: Int?
)

data class Data(
    @SerializedName("user")
    val user: User?,
    @SerializedName("access_token")
    val accessToken: String?,
    @SerializedName("refresh_token")
    val refreshToken: String?,
    @SerializedName("equipments")
    val equipments: Equipments?,
)

data class Equipments(
    @SerializedName("count")
    val count: Int?,
    @SerializedName("rows")
    val rows: MutableList<Equipment>?
)
