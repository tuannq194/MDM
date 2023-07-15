package com.ngxqt.mdm.util

import com.ngxqt.mdm.data.model.objectmodel.Equipment
import com.ngxqt.mdm.data.model.objectmodel.EquipmentSubField

internal fun isEmailValid(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

internal fun isUrlValid(url: String): Boolean {
    val regex = Regex("^(http|https)://[\\w.-]+\\.com$")
    return regex.matches(url)
}

internal fun Equipment.withDefaultValues(): Equipment {
    return this.copy(
        id = id ?: 0,
        name = name ?: "",
        model = model ?: "",
        serial = serial ?: "",
        code = code ?: "",
        hashCode = hashCode ?: "",
        image = image ?: "",
        qrCode = qrCode ?: "",
        riskLevel = riskLevel ?: 0,
        unitId = unitId ?: 0,
        technicalParameter = technicalParameter ?: "",
        warehouseImportDate = warehouseImportDate ?: "",
        yearOfManufacture = yearOfManufacture ?: 0,
        yearInUse = yearInUse ?: 0,
        configuration = configuration ?: "",
        importPrice = importPrice ?: 0.0,
        initialValue = initialValue ?: 0.0,
        annualDepreciation = annualDepreciation ?: 0.0,
        usageProcedure = usageProcedure ?: "",
        jointVentureContractExpirationDate = jointVentureContractExpirationDate ?: "",
        note = note ?: "",
        statusId = statusId ?: 0,
        manufacturerId = manufacturerId ?: "",
        manufacturingCountryId = manufacturingCountryId ?: "",
        supplierId = supplierId ?: 0,
        typeId = typeId ?: 0,
        departmentId = departmentId ?: 0,
        projectId = projectId ?: 0,
        regularMaintenance = regularMaintenance ?: 0,
        regularInspection = regularInspection ?: 0,
        regularRadiationMonitoring = regularRadiationMonitoring ?: 0,
        regularExternalInspection = regularExternalInspection ?: 0,
        regularRoomEnvironmentInspection = regularRoomEnvironmentInspection ?: 0,
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: "",
        equipmentType = equipmentType?.withDefaultValues(),
        equipmentUnit = equipmentUnit?.withDefaultValues(),
        equipmentStatus = equipmentStatus?.withDefaultValues(),
        equipmentRiskLevel = equipmentRiskLevel?.withDefaultValues(),
        department = department?.withDefaultValues(),
        equipmentId = equipmentId ?: 0,
        inventoryDate = inventoryDate ?: "",
        inventoryCreateUserId = inventoryCreateUserId ?: 0,
        inventoryApproveUserId = inventoryApproveUserId ?: 0,
        times = times ?: 0,
        status = status ?: 0,
        inventoryCreateUser = inventoryCreateUser?.withDefaultValues(),
        inventoryApproveUser = inventoryApproveUser?.withDefaultValues(),
        reason = reason ?: "",
        reportingPersonId = reportingPersonId ?: 0,
        brokenReportDate = brokenReportDate ?: "",
        approveReportPersonId = approveReportPersonId ?: 0,
        approveBrokenReportDate = approveBrokenReportDate ?: "",
        reportStatus = reportStatus ?: 0,
        reportNote = reportNote ?: "",
        providerId = providerId ?: 0,
        repairPriority = repairPriority ?: 0,
        scheduleRepairDate = scheduleRepairDate ?: "",
        scheduleRepairStatus = scheduleRepairStatus ?: 0,
        repairDate = repairDate ?: "",
        repairStatus = repairStatus ?: 0,
        done = done ?: 0,
        estimatedRepairCost = estimatedRepairCost ?: 0,
        repairCompletionDate = repairCompletionDate ?: "",
        actualRepairCost = actualRepairCost ?: 0,
        scheduleCreateUserId = scheduleCreateUserId ?: 0,
        scheduleApproveUserId = scheduleApproveUserId ?: 0,
        testUserId = testUserId ?: 0,
        provider = provider?.withDefaultValues(),
        repairStatusObj = repairStatusObj ?: ""
    )
}

private fun EquipmentSubField.withDefaultValues(): EquipmentSubField{
    return this.copy(
        id = id ?: 0,
        name = name ?: ""
    )
}