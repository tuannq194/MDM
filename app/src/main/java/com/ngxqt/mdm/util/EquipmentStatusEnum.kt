package com.ngxqt.mdm.util

enum class EquipmentStatusEnum(val id: Int, val statusName: String) {
    ALL(0, "Tất cả"),
    NEW(2, "Mới"),
    ACTIVE(3, "Đang sử dụng"),
    WAS_BROKEN(4, "Đang báo hỏng"),
    REPAIRED(5, "Đang sửa chữa"),
    INACTIVE(6, "Ngừng sử dụng"),
    LIQUIDATED(7, "Đã thanh lý")
}