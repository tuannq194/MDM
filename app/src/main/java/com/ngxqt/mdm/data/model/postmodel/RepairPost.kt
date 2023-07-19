package com.ngxqt.mdm.data.model.postmodel

import com.google.gson.annotations.SerializedName

data class RepairPost(
    @SerializedName("equipment_id") val equipmentId: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("department_id") val departmentId: Int?,
    @SerializedName("department") val department: String?,
    @SerializedName("reporting_person_id") val reportingPersonId: Int?,
    @SerializedName("broken_report_date") val brokenReportDate: String?,
    @SerializedName("report_status") val reportStatus: Int?,
    @SerializedName("reason") val reason: String?,
    @SerializedName("repair_priority") val repairPriority: Int?
)
