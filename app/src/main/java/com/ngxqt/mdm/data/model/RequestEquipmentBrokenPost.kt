package com.ngxqt.mdm.data.model

import com.google.gson.annotations.SerializedName

data class RequestEquipmentBrokenPost(
    @SerializedName("date_failure")
    val date_failure: String,
    @SerializedName("reason")
    val reason: String
)
