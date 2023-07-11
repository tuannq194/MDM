package com.ngxqt.mdm.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.model.HostResponse
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
class EquipmentDetailViewModel @Inject constructor(
    private val mdmRepository: MDMRepository, @ApplicationContext private val context: Context
) : ViewModel() {
    /**SEARCH EQUIPMENT BY ID*/
    private val _getEquipmentByIdResponseLiveData: MutableLiveData<Event<Resource<HostResponse>>> = MutableLiveData()
    val getEquipmentByIdResponseLiveData: LiveData<Event<Resource<HostResponse>>>
        get() = _getEquipmentByIdResponseLiveData

    private var getEquipmentByIdResponse: HostResponse? = null

    fun getEquipmentById(authorization: String, equipmentId: Int) = viewModelScope.launch() {
        safeGetEquipmentById(authorization,equipmentId)
    }

    private suspend fun safeGetEquipmentById(authorization: String, equipmentId: Int) {
        try {
            if(hasInternetConnection(context)){
                val response = mdmRepository.getEquipmentById(authorization, equipmentId)
                _getEquipmentByIdResponseLiveData.postValue(Event(handleGetEquipByIdResponse(response)))
            } else {
                _getEquipmentByIdResponseLiveData.postValue(Event(Resource.Error(context.getString(
                    R.string.mat_ket_noi_internet))))
            }
        } catch (e: Exception) {
            Log.e("SEARCHEQUIPBYID_API_ERROR", e.toString())
            _getEquipmentByIdResponseLiveData.postValue(Event(Resource.Error(e.toString())))
        }
    }

    private fun handleGetEquipByIdResponse(response: Response<HostResponse>): Resource<HostResponse> {
        if (response.isSuccessful) {
            Log.d("GETEQUIPBYID_RETROFIT_SUCCESS", "OK")
            response.body()?.let { resultResponse ->
                return Resource.Success(getEquipmentByIdResponse ?: resultResponse)
            }
        } else {
            Log.e("GETEQUIPBYID_RETROFIT_ERROR", response.toString())
        }
        return Resource.Error((getEquipmentByIdResponse ?: response.message()).toString())
    }

    /** HISROTY REPAIR*/
    private val _getRepairHisResLiveData: MutableLiveData<Event<Resource<HostResponse>>> = MutableLiveData()
    val getRepairHisResLiveData: LiveData<Event<Resource<HostResponse>>>
        get() = _getRepairHisResLiveData

    private var getRepairHisResponse: HostResponse? = null

    fun getRepairHistory(authorization: String, equipmentId: Int?) = viewModelScope.launch() {
        safeGetRepairHis(authorization, equipmentId)
    }

    private suspend fun safeGetRepairHis(authorization: String, equipmentId: Int?) {
        try {
            if(hasInternetConnection(context)){
                val response = mdmRepository.getRepairHistory(authorization, equipmentId)
                _getRepairHisResLiveData.postValue(Event(handleGetRepairHisResponse(response)))
            } else {
                _getRepairHisResLiveData.postValue(Event(Resource.Error("Mất Kết Nối Internet")))
            }
        } catch (e: Exception) {
            Log.e("GET_REPAIR_HIS_API_ERROR", e.toString())
            _getRepairHisResLiveData.postValue(Event(Resource.Error(e.toString())))
        }
    }

    private fun handleGetRepairHisResponse(response: Response<HostResponse>): Resource<HostResponse> {
        if (response.isSuccessful) {
            Log.d("GET_REPAIR_HIS_API_SUCCESS", response.body()?.message.toString())
            response.body()?.let { resultResponse ->
                return Resource.Success(getRepairHisResponse ?: resultResponse)
            }
        } else {
            Log.e("GET_REPAIR_HIS_API_ERROR", response.toString())
        }
        return Resource.Error((getRepairHisResponse ?: response.message()).toString())
    }

    /** HISROTY INVENTORY*/
    private val _getInventoryHisResLiveData: MutableLiveData<Event<Resource<HostResponse>>> = MutableLiveData()
    val getInventoryHisResponseLiveData: LiveData<Event<Resource<HostResponse>>>
        get() = _getInventoryHisResLiveData

    private var getInventoryHisResponse: HostResponse? = null

    fun getInventoryHistory(authorization: String, equipmentId: Int, page: Int?) = viewModelScope.launch() {
        safeGetInventoryHis(authorization, equipmentId, page)
    }

    private suspend fun safeGetInventoryHis(authorization: String, equipmentId: Int?, page: Int?) {
        try {
            if(hasInternetConnection(context)){
                val response = mdmRepository.getInventoryHistory(authorization, equipmentId, page)
                _getInventoryHisResLiveData.postValue(Event(handleGetAllUsersResponse(response)))
            } else {
                _getInventoryHisResLiveData.postValue(Event(Resource.Error("Mất Kết Nối Internet")))
            }
        } catch (e: Exception) {
            Log.e("GET_INVENTORY_HIS_API_ERROR", e.toString())
            _getInventoryHisResLiveData.postValue(Event(Resource.Error(e.toString())))
        }
    }

    private fun handleGetAllUsersResponse(response: Response<HostResponse>): Resource<HostResponse> {
        if (response.isSuccessful) {
            Log.d("GET_INVENTORY_HIS_API_SUCCESS", response.body()?.message.toString())
            response.body()?.let { resultResponse ->
                return Resource.Success(getInventoryHisResponse ?: resultResponse)
            }
        } else {
            Log.e("GET_INVENTORY_HIS_API_ERROR", response.toString())
        }
        return Resource.Error((getInventoryHisResponse ?: response.message()).toString())
    }
}