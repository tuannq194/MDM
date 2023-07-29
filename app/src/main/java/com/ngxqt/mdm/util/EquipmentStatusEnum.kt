package com.ngxqt.mdm.util

enum class EquipmentStatusEnum(val id: Int, val statusName: String) {
    ALL(0, "Tất Cả"),
    NEW(2, "Mới"),
    ACTIVE(3, "Đang Sử Dụng"),
    WAS_BROKEN(4, "Đang Báo Hỏng"),
    REPAIRED(5, "Đang Sửa Chữa"),
    INACTIVE(6, "Đã Thanh Lý"),
    LIQUIDATED(7, "Ngưng Sử Dụng")
}