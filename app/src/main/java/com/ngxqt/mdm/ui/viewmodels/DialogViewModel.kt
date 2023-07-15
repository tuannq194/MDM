package com.ngxqt.mdm.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ngxqt.mdm.data.model.objectmodel.Equipment
import com.ngxqt.mdm.repository.MDMRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class DialogViewModel @Inject constructor(
    private val mdmRepository: MDMRepository, @ApplicationContext private val context: Context
) : ViewModel() {
    /**GET INVENTORY HISTORY*/
    fun getInventoryHistory(
        authorization: String,
        equipmentId: Int?
    ): LiveData<PagingData<Equipment>>{
        return mdmRepository.getInventoryHistory(
            authorization,
            equipmentId
        ).cachedIn(viewModelScope)
    }

    fun getRepairHistory(
        authorization: String,
        equipmentId: Int?
    ): LiveData<PagingData<Equipment>>{
        return mdmRepository.getRepairHistory(
            authorization,
            equipmentId
        ).cachedIn(viewModelScope)
    }

    /**GET REPAIR HISTORY*/
   /* private val _getRepairHistoryResponseLiveData: MutableLiveData<Event<Resource<HostResponse>>> = MutableLiveData()
    val getRepairHistoryResponseLiveData: LiveData<Event<Resource<HostResponse>>>
        get() = _getRepairHistoryResponseLiveData

    private var getRepairHistoryResponse: HostResponse? = null

    fun getRepairHistory(authorization: String, equipmentId: Int?) = viewModelScope.launch() {
        safeGetRepaiHistory(authorization, equipmentId)
    }

    private suspend fun safeGetRepaiHistory(authorization: String, equipmentId: Int?) {
        try {
            if(NetworkUtil.hasInternetConnection(context)){
                val response = mdmRepository.getRepairHistory(authorization,equipmentId)
                _getRepairHistoryResponseLiveData.postValue(Event(handleGetRepairHistoryResponse(response)))
            } else {
                _getRepairHistoryResponseLiveData.postValue(Event(Resource.Error("Mất Kết Nối Internet")))
            }
        } catch (e: Exception) {
            Log.e("GETREPAIRHISTORY_API_ERROR", e.toString())
            _getRepairHistoryResponseLiveData.postValue(Event(Resource.Error(e.toString())))
        }
    }

    private fun handleGetRepairHistoryResponse(response: Response<HostResponse>): Resource<HostResponse> {
        if (response.isSuccessful) {
            Log.d("GETREPAIRHISTORY_RETROFIT_SUCCESS", response.body()?.data.toString())
            response.body()?.let { resultResponse ->
                return Resource.Success(getRepairHistoryResponse ?: resultResponse)
            }
        } else {
            Log.e("GETREPAIRHISTORY_RETROFIT_ERROR", response.toString())
        }
        return Resource.Error((getRepairHistoryResponse ?: response.message()).toString())
    }*/
}