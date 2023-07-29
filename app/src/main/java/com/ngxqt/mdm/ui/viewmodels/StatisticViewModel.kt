package com.ngxqt.mdm.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngxqt.mdm.data.model.responsemodel.GetAllEquipmentsResponse
import com.ngxqt.mdm.repository.MDMRepository
import com.ngxqt.mdm.util.Event
import com.ngxqt.mdm.util.LogUtils
import com.ngxqt.mdm.util.NetworkUtil
import com.ngxqt.mdm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(
    private val mdmRepository: MDMRepository, @ApplicationContext private val context: Context
): ViewModel() {

    /**GET ALL EQUIPMENT*/
    private val _getAllEquipmentsResponseLiveData: MutableLiveData<Event<Resource<GetAllEquipmentsResponse>>> = MutableLiveData()
    val getAllEquipmentsResponseLiveData: LiveData<Event<Resource<GetAllEquipmentsResponse>>>
        get() = _getAllEquipmentsResponseLiveData

    private var getAllEquipmentsResponse: GetAllEquipmentsResponse? = null

    fun getAllEquipments(authorization: String) = viewModelScope.launch() {
        safeGetAllEquipments(authorization)
    }

    private suspend fun safeGetAllEquipments(authorization: String) {
        try {
            if(NetworkUtil.hasInternetConnection(context)){
                val response = mdmRepository.getAllEquipments(authorization)
                _getAllEquipmentsResponseLiveData.postValue(Event(handleGetAllEquipmentsResponse(response)))
            } else {
                _getAllEquipmentsResponseLiveData.postValue(Event(Resource.Error("Mất Kết Nối Internet")))
            }
        } catch (e: Exception) {
            LogUtils.d("GETALLEQUIPMENT_API_ERROR: $e")
            _getAllEquipmentsResponseLiveData.postValue(Event(Resource.Error(e.toString())))
        }
    }

    private fun handleGetAllEquipmentsResponse(response: Response<GetAllEquipmentsResponse>): Resource<GetAllEquipmentsResponse> {
        if (response.isSuccessful) {
            LogUtils.d("GETALLEQUIPMENT_T_RETROFIT_SUCCESS: ${response.body()?.message}")
            response.body()?.let { resultResponse ->
                return Resource.Success(getAllEquipmentsResponse ?: resultResponse)
            }
        } else {
            LogUtils.d("GETALLEQUIPMENT__RETROFIT_ERROR: $response")
            var res = response.body()?.message.toString()
            if (response.code()==401) res = "Email Hoặc Mật Khẩu Không Đúng"
            else if (response.code()==400) res = "Invalid request body"
            else if (response.code()==500) res = "Internal server error"
            return Resource.Error(res)
        }
        return Resource.Error((getAllEquipmentsResponse ?: response.message()).toString())
    }

}