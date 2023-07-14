package com.ngxqt.mdm.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.data.model.GetAllDepartmentsResponse
import com.ngxqt.mdm.data.model.HostResponse
import com.ngxqt.mdm.data.model.User
import com.ngxqt.mdm.data.model.postmodel.InventoryPost
import com.ngxqt.mdm.data.model.postmodel.LoginPost
import com.ngxqt.mdm.data.model.postmodel.RepairPost
import com.ngxqt.mdm.data.remote.ApiInterface
import com.ngxqt.mdm.ui.paging.DepartmentsPagingSource
import com.ngxqt.mdm.ui.paging.EquipmentsPagingSource
import com.ngxqt.mdm.ui.paging.NotificationPagingSource
import com.ngxqt.mdm.ui.paging.UserPagingSource
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Response
import javax.inject.Inject

@ViewModelScoped
class MDMRepository @Inject constructor(
    private val mdmApi: ApiInterface,
    private val preferences: UserPreferences
) {
    suspend fun login(post: LoginPost): Response<HostResponse> {
        return mdmApi.userLogin(post)
    }

    suspend fun getEquipmentById(authorization: String, equipmentId: Int): Response<HostResponse> {
        return mdmApi.getEquipmentById(authorization, equipmentId)
    }

    fun getEquipments(
        authorization: String, name: String?, departmentId: Int?, statusId: Int?,
        typeId: Int?, riskLevel: Int?, yearInUse: Int?, yearOfManufacture: Int?
    ) = Pager(
        config = PagingConfig(
            pageSize = 10,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            EquipmentsPagingSource(
                mdmApi, authorization, name, departmentId, statusId,
                typeId, riskLevel, yearInUse, yearOfManufacture
            )
        }
    ).liveData

    fun getDepartments(
        authorization: String, keyword: String?
    ) = Pager(
        config = PagingConfig(
            pageSize = 10,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            DepartmentsPagingSource(
                mdmApi, authorization, keyword
            )
        }
    ).liveData

    suspend fun getAllDepartments(authorization: String): Response<GetAllDepartmentsResponse> {
        return mdmApi.getAllDepartments(authorization)
    }

    suspend fun getDepartmentById(authorization: String, departmentId: Int?): Response<HostResponse> {
        return mdmApi.getDepartmentById(authorization,departmentId)
    }

    fun getUsers(
        authorization: String, keyword: String?,
        roleId: Int?, departmentId: Int?
    ) = Pager(
        config = PagingConfig(
            pageSize = 10,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            UserPagingSource(
                mdmApi, authorization, keyword, roleId, departmentId
            )
        }
    ).liveData

    suspend fun getRepairHistory(authorization: String, equipmentId: Int?): Response<HostResponse> {
        return mdmApi.getRepairHistory(authorization, equipmentId)
    }

    suspend fun getInventoryHistory(authorization: String, equipmentId: Int?, page: Int?): Response<HostResponse> {
        return mdmApi.getInventoryHistory(authorization, equipmentId, page)
    }

    suspend fun requestInventoryEquipment(authorization: String, post: InventoryPost): Response<HostResponse> {
        val list: MutableList<InventoryPost> = mutableListOf()
        list.add(post)
        return mdmApi.requestInventoryEquipment(authorization, list)
    }

    suspend fun requestRepairEquipment(authorization: String, post: RepairPost): Response<HostResponse> {
        return mdmApi.requestRepairEquipment(authorization, post)
    }

    fun getNotification(
        authorization: String
    ) = Pager(
        config = PagingConfig(
            pageSize = 10,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            NotificationPagingSource(
                mdmApi, authorization
            )
        }
    ).liveData


    /**
     * Old function
     */
    /*suspend fun getNotification(authorization: String): Response<GetNotificationResponse> {
        return mdmApi.getNotification(authorization)
    }*/

    suspend fun saveToken(accessToken: String) {
        preferences.saveToken(accessToken)
    }

    suspend fun saveBaseUrl(baseUrl: String) {
        preferences.saveBaseUrl(baseUrl)
    }

    suspend fun saveUserInfo(user: User) {
        preferences.saveUserInfo(user)
    }

    suspend fun saveSettingPassword(isTurnOn: Boolean) {
        preferences.saveSettingPassword(isTurnOn)
    }

    suspend fun saveSettingBiometric(isTurnOn: Boolean) {
        preferences.saveSettingBiometric(isTurnOn)
    }

    suspend fun clearData() {
        preferences.clearData()
    }
}