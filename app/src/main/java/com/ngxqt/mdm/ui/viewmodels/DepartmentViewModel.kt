package com.ngxqt.mdm.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngxqt.mdm.data.model.GetAllDepartmentsResponse
import com.ngxqt.mdm.data.model.GetListEquipmentsByDepartmentIdResponse
import com.ngxqt.mdm.repository.MDMRepository
import com.ngxqt.mdm.util.Event
import com.ngxqt.mdm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class DepartmentViewModel @Inject constructor(
    private val mdmRepository: MDMRepository, @ApplicationContext private val context: Context
) : ViewModel() {

    private val _getAllDepartmentsResponseLiveData: MutableLiveData<Event<Resource<GetAllDepartmentsResponse>>> = MutableLiveData()
    val getAllDepartmentsResponseLiveData: LiveData<Event<Resource<GetAllDepartmentsResponse>>>
        get() = _getAllDepartmentsResponseLiveData

    private var getAllDepartmentsResponse: GetAllDepartmentsResponse? = null

    fun getAllDepartments(authorization: String) = viewModelScope.launch(Dispatchers.IO) {
        safeGetAllDepartments(authorization)
    }

    private suspend fun safeGetAllDepartments(authorization: String) {
        try {
            val response = mdmRepository.getAllDepartments(authorization)
            _getAllDepartmentsResponseLiveData.postValue(Event(handleGetAllUsersResponse(response)))
        } catch (e: Exception) {
            Log.e("GETALLDEPARTMENT_API_ERROR", e.toString())
            _getAllDepartmentsResponseLiveData.postValue(Event(Resource.Error(e.toString())))
        }
    }

    private fun handleGetAllUsersResponse(response: Response<GetAllDepartmentsResponse>): Resource<GetAllDepartmentsResponse> {
        if (response.isSuccessful) {
            Log.d("GETALLDEPARTMENT_RETROFIT_SUCCESS", response.body()?.dataLength.toString())
            response.body()?.let { resultResponse ->
                return Resource.Success(getAllDepartmentsResponse ?: resultResponse)
            }
        } else {
            Log.e("GETALLDEPARTMENT_RETROFIT_ERROR", response.toString())
        }
        return Resource.Error((getAllDepartmentsResponse ?: response.message()).toString())
    }


    private val _getListEquipByDepartmentResponseLiveData: MutableLiveData<Event<Resource<GetListEquipmentsByDepartmentIdResponse>>> = MutableLiveData()
    val getListEquipByDepartmentResponseLiveData: LiveData<Event<Resource<GetListEquipmentsByDepartmentIdResponse>>>
        get() = _getListEquipByDepartmentResponseLiveData

    private var getListEquipByDepartmentResponse: GetListEquipmentsByDepartmentIdResponse? = null

    fun getListEquipmentByDepartmentId(authorization: String, departmentId: Int) = viewModelScope.launch(Dispatchers.IO) {
        safeGetListEquipByDepartment(authorization, departmentId)
    }

    private suspend fun safeGetListEquipByDepartment(authorization: String, departmentId: Int) {
        try {
            val response = mdmRepository.getListEquipmentsByDepartmenId(authorization, departmentId)
            _getListEquipByDepartmentResponseLiveData.postValue(Event(handleLoginResponse(response)))
        } catch (e: Exception){
            Log.e("GETEQUIPbyDEPARTMENT_API_ERROR", e.toString())
            _getListEquipByDepartmentResponseLiveData.postValue(Event(Resource.Error(e.toString())))
        }

    }

    private fun handleLoginResponse(response: Response<GetListEquipmentsByDepartmentIdResponse>): Resource<GetListEquipmentsByDepartmentIdResponse> {
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