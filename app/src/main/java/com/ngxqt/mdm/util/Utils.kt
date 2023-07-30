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

internal fun statusNameToStatusIdMapper(statusName: String?): Int? {
    return when (statusName) {
        EquipmentStatusEnum.ALL.statusName-> EquipmentStatusEnum.ALL.id
        EquipmentStatusEnum.NEW.statusName -> EquipmentStatusEnum.NEW.id
        EquipmentStatusEnum.ACTIVE.statusName -> EquipmentStatusEnum.ACTIVE.id
        EquipmentStatusEnum.WAS_BROKEN.statusName -> EquipmentStatusEnum.WAS_BROKEN.id
        EquipmentStatusEnum.REPAIRED.statusName -> EquipmentStatusEnum.REPAIRED.id
        EquipmentStatusEnum.INACTIVE.statusName -> EquipmentStatusEnum.INACTIVE.id
        EquipmentStatusEnum.LIQUIDATED.statusName -> EquipmentStatusEnum.LIQUIDATED.id
        else -> null
    }
}

internal fun statusIdToStatusNameMapper(statusId: Int?): String {
    return when (statusId) {
        EquipmentStatusEnum.ALL.id-> EquipmentStatusEnum.ALL.statusName
        EquipmentStatusEnum.NEW.id -> EquipmentStatusEnum.NEW.statusName
        EquipmentStatusEnum.ACTIVE.id -> EquipmentStatusEnum.ACTIVE.statusName
        EquipmentStatusEnum.WAS_BROKEN.id -> EquipmentStatusEnum.WAS_BROKEN.statusName
        EquipmentStatusEnum.REPAIRED.id -> EquipmentStatusEnum.REPAIRED.statusName
        EquipmentStatusEnum.INACTIVE.id -> EquipmentStatusEnum.INACTIVE.statusName
        EquipmentStatusEnum.LIQUIDATED.id -> EquipmentStatusEnum.LIQUIDATED.statusName
        else -> ""
    }
}