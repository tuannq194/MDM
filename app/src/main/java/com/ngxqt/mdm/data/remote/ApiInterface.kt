package com.ngxqt.mdm.data.remote

import com.ngxqt.mdm.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    @POST("v1/api/auth/login")
    suspend fun userLogin(@Body post: LoginPost): Response<LoginResponse>

    @GET("api/v1/users")
    suspend fun getAllUsers(@Header("Authorization") authorization: String): Response<GetAllUsersResponse>

    @GET("api/v1/users")
    suspend fun searchUsers(@Header("Authorization") authorization: String, @Query("keyword") keyword: String?): Response<GetAllUsersResponse>

    @GET("api/v1/equipments")
    suspend fun getAllEquipments(@Header("Authorization") authorization: String): Response<GetAllEquipmentsResponse>

    @GET("api/v1/equipments")
    suspend fun searchEquipments(@Header("Authorization") authorization: String, @Query("keyword") keyword: String): Response<GetAllEquipmentsResponse>

    @GET("api/v1/equipments/{id}")
    suspend fun searchEquipmentsById(@Header("Authorization") authorization: String, @Path("id") equipmentId: Int): Response<SearchEquipmentsByIdResponse>

    @GET("api/v1/statistical-by-info")
    suspend fun statisticalEquipments(@Header("Authorization") authorization: String, @Query("status") status: String): Response<StatisticalEquipmentsResponse>

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