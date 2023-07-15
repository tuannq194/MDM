package com.ngxqt.mdm.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngxqt.mdm.data.model.responsemodel.HostResponse
import com.ngxqt.mdm.repository.MDMRepository
import com.ngxqt.mdm.util.Event
import com.ngxqt.mdm.util.NetworkUtil
import com.ngxqt.mdm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val mdmRepository: MDMRepository, @ApplicationContext private val context: Context
) : ViewModel() {
    /**GET DEPARTMENT BY ID*/
    private val _getDepartmentByIdResponseLiveData: MutableLiveData<Event<Resource<HostResponse>>> = MutableLiveData()
    val getDepartmentByIdResponseLiveData: LiveData<Event<Resource<HostResponse>>>
        get() = _getDepartmentByIdResponseLiveData

    private var getDepartmentByIdResponse: HostResponse? = null

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

    private fun handleGetDepartmentByIdResponse(response: Response<HostResponse>): Resource<HostResponse> {
        if (response.isSuccessful) {
            Log.d("GETDEPARTMENT_RETROFIT_SUCCESS", response.body()?.data.toString())
            response.body()?.let { resultResponse ->
                return Resource.Success(getDepartmentByIdResponse ?: resultResponse)
            }
        } else {
            Log.e("GETDEPARTMENT_RETROFIT_ERROR", response.toString())
        }
        return Resource.Error((getDepartmentByIdResponse ?: response.message()).toString())
    }

    fun clearData() {
        viewModelScope.launch {
            mdmRepository.clearData()
        }
    }
}