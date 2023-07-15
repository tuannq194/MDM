package com.ngxqt.mdm.data.model.responsemodel

import com.google.gson.annotations.SerializedName
import com.ngxqt.mdm.data.model.objectmodel.Department

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
