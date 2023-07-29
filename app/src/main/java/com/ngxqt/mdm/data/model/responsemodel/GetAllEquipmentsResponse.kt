package com.ngxqt.mdm.data.model.responsemodel

import com.google.gson.annotations.SerializedName
import com.ngxqt.mdm.data.model.objectmodel.Equipment

data class GetAllEquipmentsResponse(
    @SerializedName("success") val success: Boolean?,
    @SerializedName("data") val data: DataAllEquipments?,
    @SerializedName("message") val message: String?,
    @SerializedName("code") val code: Int?
)

data class DataAllEquipments(
    @SerializedName("count") val count: Int?,
    @SerializedName("equipments") val equipments: MutableList<Equipment>?
)
