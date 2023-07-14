package com.ngxqt.mdm.data.remote

import com.ngxqt.mdm.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    @POST("v1/api/auth/login")
    suspend fun userLogin(@Body post: LoginPost): Response<LoginResponse>

    @GET("v1/api/equipment/detail")
    suspend fun getEquipmentById(@Header("Authorization") authorization: String, @Query("id") equipmentId: Int): Response<HostResponse>

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

    @GET("v1/api/equipment_repair/history_repair")
    suspend fun getRepairHistory(
        @Header("Authorization") authorization: String,
        @Query("id") equipment_id: Int? = null,
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



    @GET("api/v1/users")
    suspend fun getAllUsers(@Header("Authorization") authorization: String): Response<GetAllUsersResponse>

    @GET("api/v1/users")
    suspend fun searchUsers(@Header("Authorization") authorization: String, @Query("keyword") keyword: String?): Response<GetAllUsersResponse>

    @GET("api/v1/equipments")
    suspend fun getAllEquipments(@Header("Authorization") authorization: String): Response<GetAllEquipmentsResponse>

    @GET("api/v1/equipments")
    suspend fun searchEquipments(@Header("Authorization") authorization: String, @Query("keyword") keyword: String): Response<GetAllEquipmentsResponse>

    @GET("api/v1/departments/{id}")
    suspend fun getDepartmentById(@Header("Authorization") authorization: String, @Path("id") departmentId: Int?): Response<GetDepartmentByIdResponse>

    @GET("api/v1/listEquipmentInventory/{id}")
    suspend fun getListEquipmentsByDepartmenId(@Header("Authorization") authorization: String, @Path("id") departmentId: Int): Response<GetListEquipmentsByDepartmentIdResponse>

    @GET("api/v1/listInventoryByEquipmentID/{id}")
    suspend fun getListInventoryById(@Header("Authorization") authorization: String, @Path("id") equipmentId: Int): Response<GetListInventoryResponse>

    @POST("api/v1/equipment/{id}")
    suspend fun requestEquipmentBroken(@Header("Authorization") authorization: String, @Path("id") equipmentId: Int, @Body post: RequestEquipmentBrokenPost): Response<RequestEquipmentBrokenResponse>

    @POST("api/v1/createInventory/{id}")
    suspend fun requestEquipmentInventory(@Header("Authorization") authorization: String, @Path("id") equipmentId: Int, @Body post: RequestEquipmentInventoryPost): Response<RequestEquipmentInventoryResponse>

    @GET("api/v1/notification")
    suspend fun getNotification(@Header("Authorization") authorization: String): Response<GetNotificationResponse>
}