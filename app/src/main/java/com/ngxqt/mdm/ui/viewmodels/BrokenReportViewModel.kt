package com.ngxqt.mdm.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngxqt.mdm.data.model.responsemodel.HostResponse
import com.ngxqt.mdm.data.model.postmodel.RepairPost
import com.ngxqt.mdm.repository.MDMRepository
import com.ngxqt.mdm.util.CoroutineDispatcherProvider
import com.ngxqt.mdm.util.Event
import com.ngxqt.mdm.util.NetworkUtil
import com.ngxqt.mdm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class BrokenReportViewModel @Inject constructor(
    private val mdmRepository: MDMRepository,
    private val dispatcherProvider: CoroutineDispatcherProvider,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _brokenReportResponseLiveData: MutableLiveData<Event<Resource<HostResponse>>> = MutableLiveData()
    val brokenReportResponseLiveData: LiveData<Event<Resource<HostResponse>>>
        get() = _brokenReportResponseLiveData

    private var brokenReportResponse: HostResponse? = null

    fun brokenReport(authorization: String, post: RepairPost) = viewModelScope.launch() {
        safeBrokenReport(authorization, post)
    }

    private suspend fun safeBrokenReport(authorization: String, post: RepairPost) {
        try {
            if(NetworkUtil.hasInternetConnection(context)){
                val response = mdmRepository.requestRepairEquipment(authorization, post)
                _brokenReportResponseLiveData.postValue(Event(handleLoginResponse(response)))
            } else {
                _brokenReportResponseLiveData.postValue(Event(Resource.Error("Mất Kết Nối Internet")))
            }
        } catch (e: Exception){
            Log.e("REPAIR_API_ERROR", e.toString())
            _brokenReportResponseLiveData.postValue(Event(Resource.Error(e.toString())))
        }

    }

    private fun handleLoginResponse(response: Response<HostResponse>): Resource<HostResponse> {
        if (response.isSuccessful) {
            Log.d("REPAIR_RETROFIT_SUCCESS", response.body().toString())
            response.body()?.let { resultResponse ->
                return Resource.Success(brokenReportResponse ?: resultResponse)
            }
        } else {
            Log.e("REPAIR_RETROFIT_ERROR", response.toString())
        }
        return Resource.Error((brokenReportResponse ?: response.message()).toString())
    }
}