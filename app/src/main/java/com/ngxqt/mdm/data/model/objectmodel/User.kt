package com.ngxqt.mdm.data.model.objectmodel

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("password")
    val password: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("department_id")
    val departmentId: Int?,
    @SerializedName("role_id")
    val roleId: Int?,
    @SerializedName("is_active")
    val isActive: Int?,
    @SerializedName("active_token")
    val activeToken: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("Role")
    val role: Role?,
    @SerializedName("Department")
    val department: Department?
)

data class Role(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("Role_Permissions")
    val rolePermissions: MutableList<RolePermission>?
)

data class RolePermission(
    @SerializedName("permission_id")
    val permissionId: Int?,
    @SerializedName("Permission")
    val permission: Permission?
)

data class Permission(
    @SerializedName("name")
    val name: String?,
    @SerializedName("display_name")
    val displayName: String?
)