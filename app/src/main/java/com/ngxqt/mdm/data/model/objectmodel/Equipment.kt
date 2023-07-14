package com.ngxqt.mdm.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Equipment(
    // Field of EquipmentI
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("model") val model: String?,
    @SerializedName("serial") val serial: String?,
    @SerializedName("code") val code: String?,
    @SerializedName("hash_code") val hashCode: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("qrcode") val qrCode: String?,
    @SerializedName("risk_level") val riskLevel: Int?,
    @SerializedName("unit_id") val unitId: Int?,
    @SerializedName("technical_parameter") val technicalParameter: String?,
    @SerializedName("warehouse_import_date") val warehouseImportDate: String?,
    @SerializedName("year_of_manufacture") val yearOfManufacture: Int?,
    @SerializedName("year_in_use") val yearInUse: Int?,
    @SerializedName("configuration") val configuration: String?,
    @SerializedName("import_price") val importPrice: Double?,
    @SerializedName("initial_value") val initialValue: Double?,
    @SerializedName("annual_depreciation") val annualDepreciation: Double?,
    @SerializedName("usage_procedure") val usageProcedure: String?,
    @SerializedName("joint_venture_contract_expiration_date") val jointVentureContractExpirationDate: String?,
    @SerializedName("note") val note: String?,
    @SerializedName("status_id") val statusId: Int?,
    @SerializedName("manufacturer_id") val manufacturerId: String?,
    @SerializedName("manufacturing_country_id") val manufacturingCountryId: String?,
    @SerializedName("supplier_id") val supplierId: Int?,
    @SerializedName("type_id") val typeId: Int?,
    @SerializedName("department_id") val departmentId: Int?,
    @SerializedName("project_id") val projectId: Int?,
    @SerializedName("regular_maintenance") val regularMaintenance: Int?,
    @SerializedName("regular_inspection") val regularInspection: Int?,
    @SerializedName("regular_radiation_monitoring") val regularRadiationMonitoring: Int?,
    @SerializedName("regular_external_inspection") val regularExternalInspection: Int?,
    @SerializedName("regular_room_environment_inspection") val regularRoomEnvironmentInspection: Int?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?,
    @SerializedName("Equipment_Type") val equipmentType: EquipmentSubField?,
    @SerializedName("Equipment_Unit") val equipmentUnit: EquipmentSubField?,
    @SerializedName("Equipment_Status") val equipmentStatus: EquipmentSubField?,
    @SerializedName("Equipment_Risk_Level") val equipmentRiskLevel: EquipmentSubField?,
    @SerializedName("Department") val department: EquipmentSubField?,

    // More field for Equipment Inventory History
    @SerializedName("equipment_id") val equipmentId: Int?,
    @SerializedName("inventory_date") val inventoryDate: String?,
    @SerializedName("inventory_create_user_id") val inventoryCreateUserId: Int?,
    @SerializedName("inventory_approve_user_id") val inventoryApproveUserId: Int?,
    @SerializedName("times") val times: Int?,
    @SerializedName("status") val status: Int?,
    @SerializedName("inventory_create_user") val inventoryCreateUser: EquipmentSubField?,
    @SerializedName("inventory_approve_user") val inventoryApproveUser: EquipmentSubField?
) : Parcelable

@Parcelize
data class EquipmentSubField(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?
) : Parcelable
