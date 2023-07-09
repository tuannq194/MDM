package com.ngxqt.mdm.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ngxqt.mdm.data.model.*
import com.ngxqt.mdm.repository.MDMRepository
import com.ngxqt.mdm.util.Event
import com.ngxqt.mdm.util.NetworkUtil
import com.ngxqt.mdm.util.NetworkUtil.Companion.hasInternetConnection
import com.ngxqt.mdm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class DepartmentViewModel @Inject constructor(
    private val mdmRepository: MDMRepository, @ApplicationContext private val context: Context
) : ViewModel() {
    fun getDepartments(
        authorization: String,
        keyword: String? = null
    ): LiveData<PagingData<Department>>{
        return mdmRepository.getDepartments(
            authorization, keyword
        ).cachedIn(viewModelScope)
    }

    /**GET DEPARTMENT BY ID*/
    private val _getDepartmentByIdResponseLiveData: MutableLiveData<Event<Resource<GetDepartmentByIdResponse>>> = MutableLiveData()
    val getDepartmentByIdResponseLiveData: LiveData<Event<Resource<GetDepartmentByIdResponse>>>
        get() = _getDepartmentByIdResponseLiveData

    private var getDepartmentByIdResponse: GetDepartmentByIdResponse? = null

    fun getDepartmentById(authorization: String, departmentId: Int?) = viewModelScope.launch() {
        safeGetDepartmentById(authorization, departmentId)
    }

    private suspend fun safeGetDepartmentById(authorization: String, departmentId: Int?) {
        try {
            if(NetworkUtil.hasInternetConnection(context)){
                val response = mdmRepository.getDepartmentById(authorization,departmentId)
                _getDepartmentByIdResponseLiveData.postValue(Event(handleGetDepartmentByIdResponse(response)))
            } else {
                _getDepartmentByIdResponseLiveData.postValue(Event(Resource.Error("Mất Kết Nối Internet")))
            }
        } catch (e: Exception) {
            Log.e("GETDEPARTMENT_API_ERROR", e.toString())
            _getDepartmentByIdResponseLiveData.postValue(Event(Resource.Error(e.toString())))
        }
    }

    private fun handleGetDepartmentByIdResponse(response: Response<GetDepartmentByIdResponse>): Resource<GetDepartmentByIdResponse> {
        if (response.isSuccessful) {
            Log.d("GETDEPARTMENT_RETROFIT_SUCCESS", response.body()?.dataLength.toString())
            response.body()?.let { resultResponse ->
                return Resource.Success(getDepartmentByIdResponse ?: resultResponse)
            }
        } else {
            Log.e("GETDEPARTMENT_RETROFIT_ERROR", response.toString())
        }
        return Resource.Error((getDepartmentByIdResponse ?: response.message()).toString())
    }

    /**GET LIST EQUIPMENT BY DEPARTMENT*/
    private val _getListEquipByDepartmentResponseLiveData: MutableLiveData<Event<Resource<GetListEquipmentsByDepartmentIdResponse>>> = MutableLiveData()
    val getListEquipByDepartmentResponseLiveData: LiveData<Event<Resource<GetListEquipmentsByDepartmentIdResponse>>>
        get() = _getListEquipByDepartmentResponseLiveData

    private var getListEquipByDepartmentResponse: GetListEquipmentsByDepartmentIdResponse? = null

    fun getListEquipmentByDepartmentId(authorization: String, departmentId: Int) = viewModelScope.launch() {
        safeGetListEquipByDepartment(authorization, departmentId)
    }

    private suspend fun safeGetListEquipByDepartment(authorization: String, departmentId: Int) {
        try {
            val response = mdmRepository.getListEquipmentsByDepartmenId(authorization, departmentId)
            _getListEquipByDepartmentResponseLiveData.postValue(Event(handleGetListEquipByDepartmentResponse(response)))
        } catch (e: Exception){
            Log.e("GETEQUIPbyDEPARTMENT_API_ERROR", e.toString())
            _getListEquipByDepartmentResponseLiveData.postValue(Event(Resource.Error(e.toString())))
        }

    }

    private fun handleGetListEquipByDepartmentResponse(response: Response<GetListEquipmentsByDepartmentIdResponse>): Resource<GetListEquipmentsByDepartmentIdResponse> {
        if (response.isSuccessful) {
            Log.d("GETEQUIPbyDEPARTMENT_RETROFIT_SUCCESS", response.body()?.dataLength.toString())
            response.body()?.let { resultResponse ->
                return Resource.Success(getListEquipByDepartmentResponse ?: resultResponse)
            }
        } else {
            Log.e("GETEQUIPbyDEPARTMENT_RETROFIT_ERROR", response.toString())
        }
        return Resource.Error((getListEquipByDepartmentResponse ?: response.message()).toString())
    }
}