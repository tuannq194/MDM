package com.ngxqt.mdm.data.model.postmodel

import com.google.gson.annotations.SerializedName

data class InventoryPost(
    @SerializedName("equipment_id") val equipmentId: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("department") val department: String?,
    @SerializedName("inventory_create_user_id") val inventoryCreateUserId: Int?,
    @SerializedName("inventory_date") val inventoryDate: String?,
    @SerializedName("status") val status: Int?,
    @SerializedName("times") val times: Int?,
    @SerializedName("note") val note: String?
)
