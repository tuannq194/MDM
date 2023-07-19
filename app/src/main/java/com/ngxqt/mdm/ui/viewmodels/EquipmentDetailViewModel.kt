package com.ngxqt.mdm.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.model.responsemodel.HostResponse
import com.ngxqt.mdm.repository.MDMRepository
import com.ngxqt.mdm.util.Event
import com.ngxqt.mdm.util.LogUtils
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
            LogUtils.d("SEARCHEQUIPBYID_API_ERROR: $e")
            _getEquipmentByIdResponseLiveData.postValue(Event(Resource.Error(e.toString())))
        }
    }

    private fun handleGetEquipByIdResponse(response: Response<HostResponse>): Resource<HostResponse> {
        if (response.isSuccessful) {
            LogUtils.d("GETEQUIPBYID_RETROFIT_SUCCESS: OK")
            response.body()?.let { resultResponse ->
                return Resource.Success(getEquipmentByIdResponse ?: resultResponse)
            }
        } else {
            LogUtils.d("GETEQUIPBYID_RETROFIT_ERROR: $response")
        }
        return Resource.Error((getEquipmentByIdResponse ?: response.message()).toString())
    }
}