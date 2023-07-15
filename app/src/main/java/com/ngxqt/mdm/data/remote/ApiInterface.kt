package com.ngxqt.mdm.data.remote

import com.ngxqt.mdm.data.model.*
import com.ngxqt.mdm.data.model.postmodel.InventoryPost
import com.ngxqt.mdm.data.model.postmodel.LoginPost
import com.ngxqt.mdm.data.model.postmodel.RepairPost
import com.ngxqt.mdm.data.model.responsemodel.GetAllDepartmentsResponse
import com.ngxqt.mdm.data.model.responsemodel.HostResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    @POST("v1/api/auth/login")
    suspend fun userLogin(
        @Body post: LoginPost
    ): Response<HostResponse>

    @GET("v1/api/equipment/detail")
    suspend fun getEquipmentById(
        @Header("Authorization") authorization: String,
        @Query("id") equipmentId: Int
    ): Response<HostResponse>

    @GET("v1/api/equipment/search")
    suspend fun getEquipments(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int? = null,
        @Query("name") name: String? = null,
        @Query("department_id") departmentId: Int? = null,
        @Query("status_id") statusId: Int? = null,
        @Query("type_id") typeId: Int? = null,
        @Query("risk_level") riskLevel: Int? = null,
        @Query("year_in_use") yearInUse: Int? = null,
        @Query("year_of_manufacture") yearOfManufacture: Int? = null
    ): Response<HostResponse>

    @GET("v1/api/department/search")
    suspend fun getDepartments(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int? = null,
        @Query("keyword") keyword: String? = null
    ): Response<HostResponse>

    @GET("v1/api/department/search")
    suspend fun getAllDepartments(
        @Header("Authorization") authorization: String
    ): Response<GetAllDepartmentsResponse>

    @GET("/v1/api/department/detail")
    suspend fun getDepartmentById(
        @Header("Authorization") authorization: String,
        @Query("id") departmentId: Int?
    ): Response<HostResponse>

    @GET("/v1/api/user/search")
    suspend fun getUsers(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int? = null,
        @Query("keyword") keyword: String? = null,
        @Query("role_id") roleId: Int? = null,
        @Query("department_id") departmentId: Int? = null
    ): Response<HostResponse>

    @GET("v1/api/equipment_repair/history_repair")
    suspend fun getRepairHistory(
        @Header("Authorization") authorization: String,
        @Query("id") equipmentId: Int? = null
    ): Response<HostResponse>

    @GET("v1/api/equipment_inventory/history_inventory_of_equipment")
    suspend fun getInventoryHistory(
        @Header("Authorization") authorization: String,
        @Query("equipment_id") equipmentId: Int? = null,
        @Query("page") page: Int? = null
    ): Response<HostResponse>

    @POST("v1/api/equipment_inventory/create_inventory_notes")
    suspend fun requestInventoryEquipment(
        @Header("Authorization") authorization: String,
        @Body post: List<InventoryPost>
    ): Response<HostResponse>

    @POST("v1/api/equipment_repair/report")
    suspend fun requestRepairEquipment(
        @Header("Authorization") authorization: String,
        @Body post: RepairPost
    ): Response<HostResponse>

    @GET("v1/api/notification/list")
    suspend fun getNotification(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int? = null
    ): Response<HostResponse>
}