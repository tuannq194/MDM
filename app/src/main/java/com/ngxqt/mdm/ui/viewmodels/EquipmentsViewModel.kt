package com.ngxqt.mdm.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.model.*
import com.ngxqt.mdm.repository.MDMRepository
import com.ngxqt.mdm.util.Event
import com.ngxqt.mdm.util.NetworkUtil.Companion.hasInternetConnection
import com.ngxqt.mdm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class EquipmentsViewModel @Inject constructor(
    private val mdmRepository: MDMRepository, @ApplicationContext private val context: Context
) : ViewModel() {
    //GET EQUIPMENTS V2
    fun getEquipments(
        authorization: String,
        name: String? = null,
        departmentId: Int? = null,
        statusId: Int? = null,
        typeId: Int? = null,
        riskLevel: Int? = null,
        yearInUse: Int? = null,
        yearOfManufacture: Int? = null
    ): LiveData<PagingData<Equipment>>{
        return mdmRepository.getEquipments(
            authorization, name, departmentId, statusId,
            typeId, riskLevel, yearInUse, yearOfManufacture
        ).cachedIn(viewModelScope)
    }

    //SEARCH EQUIPMENT BY ID
    private val _searchEquipmentsByIdResponseLiveData: MutableLiveData<Event<Resource<SearchEquipmentsByIdResponse>>> = MutableLiveData()
    val searchEquipmentsByIdResponseLiveData: LiveData<Event<Resource<SearchEquipmentsByIdResponse>>>
        get() = _searchEquipmentsByIdResponseLiveData

    private var searchEquipmentsByIdResponse: SearchEquipmentsByIdResponse? = null

    fun searchEquipmentsById(authorization: String, equipmentId: Int) = viewModelScope.launch() {
        safeSearchEquipmentsById(authorization,equipmentId)
    }

    private suspend fun safeSearchEquipmentsById(authorization: String, equipmentId: Int) {
        try {
            if(hasInternetConnection(context)){
                val response = mdmRepository.searchEquipmentsById(authorization, equipmentId)
                _searchEquipmentsByIdResponseLiveData.postValue(Event(handleSearchEquipByIdResponse(response)))
            } else {
                _searchEquipmentsByIdResponseLiveData.postValue(Event(Resource.Error(context.getString(R.string.mat_ket_noi_internet))))
            }
        } catch (e: Exception) {
            Log.e("SEARCHEQUIPBYID_API_ERROR", e.toString())
            _searchEquipmentsByIdResponseLiveData.postValue(Event(Resource.Error(e.toString())))
        }
    }

    private fun handleSearchEquipByIdResponse(response: Response<SearchEquipmentsByIdResponse>): Resource<SearchEquipmentsByIdResponse> {
        if (response.isSuccessful) {
            Log.d("SEARCHEQUIPBYID_RETROFIT_SUCCESS", "OK")
            response.body()?.let { resultResponse ->
                return Resource.Success(searchEquipmentsByIdResponse ?: resultResponse)
            }
        } else {
            Log.e("SEARCHEQUIPBYID_RETROFIT_ERROR", response.toString())
        }
        return Resource.Error((searchEquipmentsByIdResponse ?: response.message()).toString())
    }

    /**GET ALL DEPARTMENT*/
    private val _getAllDepartmentsResponseLiveData: MutableLiveData<Event<Resource<GetAllDepartmentsResponse>>> = MutableLiveData()
    val getAllDepartmentsResponseLiveData: LiveData<Event<Resource<GetAllDepartmentsResponse>>>
        get() = _getAllDepartmentsResponseLiveData

    private var getAllDepartmentsResponse: GetAllDepartmentsResponse? = null

    fun getAllDepartments(authorization: String) = viewModelScope.launch() {
        safeGetAllDepartments(authorization)
    }

    private suspend fun safeGetAllDepartments(authorization: String) {
        try {
            if(hasInternetConnection(context)){
                val response = mdmRepository.getAllDepartments(authorization)
                _getAllDepartmentsResponseLiveData.postValue(Event(handleGetAllDepartmentsResponse(response)))
            } else {
                _getAllDepartmentsResponseLiveData.postValue(Event(Resource.Error("Mất Kết Nối Internet")))
            }
        } catch (e: Exception) {
            Log.e("GETALLDEPARTMENT_API_ERROR", e.toString())
            _getAllDepartmentsResponseLiveData.postValue(Event(Resource.Error(e.toString())))
        }
    }

    private fun handleGetAllDepartmentsResponse(response: Response<GetAllDepartmentsResponse>): Resource<GetAllDepartmentsResponse> {
        if (response.isSuccessful) {
            Log.d("GETALLDEPARTMENT_RETROFIT_SUCCESS", response.body()?.message.toString())
            response.body()?.let { resultResponse ->
                return Resource.Success(getAllDepartmentsResponse ?: resultResponse)
            }
        } else {
            Log.e("GETALLDEPARTMENT_RETROFIT_ERROR", response.toString())
        }
        return Resource.Error((getAllDepartmentsResponse ?: response.message()).toString())
    }


}