package com.ngxqt.mdm.data.model

import com.google.gson.annotations.SerializedName
import com.ngxqt.mdm.data.model.objectmodel.Notification

data class HostResponse(
    @SerializedName("success") val success: Boolean?,
    @SerializedName("data") val data: Data?,
    @SerializedName("message") val message: String?,
    @SerializedName("code") val code: Int?
)

data class Data(
    @SerializedName("user") val user: User?,
    @SerializedName("users") val users: Users?,
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("refresh_token") val refreshToken: String?,
    @SerializedName("equipments") val equipments: Equipments?,
    @SerializedName("equipment") val equipment: Equipment?,
    @SerializedName("departments") val departments: Departments?,
    @SerializedName("department") val department: Department?,
    @SerializedName("notifications") val notifications: Notifications?,
    @SerializedName("repair_info") val repairInfo: MutableList<RepairInfo>?,
    @SerializedName("count") val count: Int?
)

data class Equipments(
    @SerializedName("count") val count: Int?,
    @SerializedName("rows") val rows: MutableList<Equipment>?
)

data class Departments(
    @SerializedName("count") val count: Int?,
    @SerializedName("rows") val rows: MutableList<Department>?
)

data class Users(
    @SerializedName("count") val count: Int?,
    @SerializedName("rows") val rows: MutableList<User>?
)

data class Notifications(
    @SerializedName("count") val count: Int?,
    @SerializedName("rows") val rows: MutableList<Notification>?
)
