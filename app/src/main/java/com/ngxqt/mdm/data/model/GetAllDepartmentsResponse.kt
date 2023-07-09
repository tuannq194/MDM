package com.ngxqt.mdm.data.model

import com.google.gson.annotations.SerializedName

data class GetAllDepartmentsResponse(
    @SerializedName("success") val success: Boolean?,
    @SerializedName("data") val data: DataAllDepartments?,
    @SerializedName("message") val message: String?,
    @SerializedName("code") val code: Int?
)

data class DataAllDepartments(
    @SerializedName("count") val count: Int?,
    @SerializedName("departments") val departments: MutableList<Department>?
)
