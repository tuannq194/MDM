package com.ngxqt.mdm.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngxqt.mdm.data.model.HostResponse
import com.ngxqt.mdm.data.model.postmodel.InventoryPost
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
class InventoryNoteViewModel @Inject constructor(
    private val mdmRepository: MDMRepository, @ApplicationContext private val context: Context
) : ViewModel() {
    private val _inventoryNoteResponseLiveData: MutableLiveData<Event<Resource<HostResponse>>> = MutableLiveData()
    val inventoryNoteResponseLiveData: LiveData<Event<Resource<HostResponse>>>
        get() = _inventoryNoteResponseLiveData

    private var inventoryNoteResponse: HostResponse? = null

    fun inventoryNote(authorization: String, post: InventoryPost) = viewModelScope.launch() {
        safeInventoryNote(authorization, post)
    }

    private suspend fun safeInventoryNote(authorization: String, post: InventoryPost) {
        try {
            if(hasInternetConnection(context)){
                val response = mdmRepository.requestInventoryEquipment(authorization, post)
                _inventoryNoteResponseLiveData.postValue(Event(handleInventoryNoteResponse(response)))
            } else {
                _inventoryNoteResponseLiveData.postValue(Event(Resource.Error("Mất Kết Nối Internet")))
            }
        } catch (e: Exception){
            Log.e("INVENTORY_API_ERROR", e.toString())
            _inventoryNoteResponseLiveData.postValue(Event(Resource.Error(e.toString())))
        }

    }

    private fun handleInventoryNoteResponse(response: Response<HostResponse>): Resource<HostResponse> {
        if (response.isSuccessful) {
            Log.d("INVENTORY_RETROFIT_SUCCESS", "OK")
            response.body()?.let { resultResponse ->
                return Resource.Success(inventoryNoteResponse ?: resultResponse)
            }
        } else {
            Log.e("INVENTORY_RETROFIT_ERROR", response.toString())
        }
        return Resource.Error((inventoryNoteResponse ?: response.message()).toString())
    }
}