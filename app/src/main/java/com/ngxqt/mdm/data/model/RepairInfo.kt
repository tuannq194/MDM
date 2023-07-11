package com.ngxqt.mdm.data.model

import com.google.gson.annotations.SerializedName

data class RepairInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("equipment_id") val equipmentId: Int,
    @SerializedName("code") val code: String,
    @SerializedName("reason") val reason: String,
    @SerializedName("reporting_person_id") val reportingPersonId: Int,
    @SerializedName("broken_report_date") val brokenReportDate: String,
    @SerializedName("approve_report_person_id") val approveReportPersonId: Int?,
    @SerializedName("approve_broken_report_date") val approveBrokenReportDate: String?,
    @SerializedName("report_status") val reportStatus: Int,
    @SerializedName("report_note") val reportNote: String?,
    @SerializedName("provider_id") val providerId: Int,
    @SerializedName("repair_priority") val repairPriority: Int,
    @SerializedName("schedule_repair_date") val scheduleRepairDate: String,
    @SerializedName("schedule_repair_status") val scheduleRepairStatus: Int,
    @SerializedName("repair_date") val repairDate: String?,
    @SerializedName("repair_status") val repairStatus: Int?,
    @SerializedName("done") val done: Int,
    @SerializedName("estimated_repair_cost") val estimatedRepairCost: Int,
    @SerializedName("repair_completion_date") val repairCompletionDate: String?,
    @SerializedName("actual_repair_cost") val actualRepairCost: Int?,
    @SerializedName("schedule_create_user_id") val scheduleCreateUserId: Int,
    @SerializedName("schedule_approve_user_id") val scheduleApproveUserId: Int?,
    @SerializedName("test_user_id") val testUserId: Int?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("Provider") val provider: Provider,
    @SerializedName("Repair_Status") val repairStatusObj: String?
)

data class Provider(
    @SerializedName("id") val id :Int?,
    @SerializedName("name") val name :String?
)
