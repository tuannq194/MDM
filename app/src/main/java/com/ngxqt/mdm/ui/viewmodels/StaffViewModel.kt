package com.ngxqt.mdm.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngxqt.mdm.data.model.GetAllUsersResponse
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
class StaffViewModel @Inject constructor(
    private val mdmRepository: MDMRepository, @ApplicationContext private val context: Context
) : ViewModel() {
    private val _getAllUsersResponseLiveData: MutableLiveData<Event<Resource<GetAllUsersResponse>>> = MutableLiveData()
    val getAllUsersResponseLiveData: LiveData<Event<Resource<GetAllUsersResponse>>>
        get() = _getAllUsersResponseLiveData

    private var getAllUsersResponse: GetAllUsersResponse? = null

    fun getAllUsers(authorization: String) = viewModelScope.launch(Dispatchers.IO) {
        safeGetAllUsers(authorization)
    }

    private suspend fun safeGetAllUsers(authorization: String) {
        try {
            val response = mdmRepository.getAllUsers(authorization)
            _getAllUsersResponseLiveData.postValue(Event(handleGetAllUsersResponse(response)))
        } catch (e: Exception) {
            Log.e("GETALLUSERS_API_ERROR", e.toString())
            _getAllUsersResponseLiveData.postValue(Event(Resource.Error(e.toString())))
        }
    }

    private fun handleGetAllUsersResponse(response: Response<GetAllUsersResponse>): Resource<GetAllUsersResponse> {
        if (response.isSuccessful) {
            Log.d("GETALLUSERS_RETROFIT_SUCCESS", response.body()?.dataLength.toString())
            response.body()?.let { resultResponse ->
                return Resource.Success(getAllUsersResponse ?: resultResponse)
            }
        } else {
            Log.e("GETALLUSERS_RETROFIT_ERROR", response.toString())
        }
        return Resource.Error((getAllUsersResponse ?: response.message()).toString())
    }


    private val _searchUsersResponseLiveData: MutableLiveData<Event<Resource<GetAllUsersResponse>>> = MutableLiveData()
    val searchUsersResponseLiveData: LiveData<Event<Resource<GetAllUsersResponse>>>
        get() = _searchUsersResponseLiveData

    private var searchUsersResponse: GetAllUsersResponse? = null

    fun searchUsers(authorization: String, keyword: String) = viewModelScope.launch(Dispatchers.IO) {
        safeSearchUsers(authorization,keyword)
    }

    private suspend fun safeSearchUsers(authorization: String, keyword: String) {
        try {
            val response = mdmRepository.searchUsers(authorization, keyword)
            _searchUsersResponseLiveData.postValue(Event(handleSearchUsersResponse(response)))
        } catch (e: Exception) {
            Log.e("SEARCHUSER_API_ERROR", e.toString())
            _searchUsersResponseLiveData.postValue(Event(Resource.Error(e.toString())))
        }
    }

    private fun handleSearchUsersResponse(response: Response<GetAllUsersResponse>): Resource<GetAllUsersResponse> {
        if (response.isSuccessful) {
            Log.d("SEARCHUSER_RETROFIT_SUCCESS", response.body()?.dataLength.toString())
            response.body()?.let { resultResponse ->
                return Resource.Success(searchUsersResponse ?: resultResponse)
            }
        } else {
            Log.e("SEARCHUSER_RETROFIT_ERROR", response.toString())
        }
        return Resource.Error((searchUsersResponse ?: response.message()).toString())
    }
}