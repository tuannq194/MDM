package com.ngxqt.mdm.data.model.responsemodel

import com.google.gson.annotations.SerializedName
import com.ngxqt.mdm.data.model.objectmodel.Equipment

data class GetHistoryRepairResponse(
    @SerializedName("success") val success: Boolean?,
    @SerializedName("data") val data: DataHistoryRepair?,
    @SerializedName("message") val message: String?,
    @SerializedName("code") val code: Int?
)

data class DataHistoryRepair(
    @SerializedName("equipment") val equipment: MutableList<Equipment>?
)
