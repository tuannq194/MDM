package com.ngxqt.mdm.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngxqt.mdm.data.model.LoginPost
import com.ngxqt.mdm.data.model.LoginResponse
import com.ngxqt.mdm.data.model.User
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
class LoginViewModel @Inject constructor(
    private val mdmRepository: MDMRepository, @ApplicationContext private val context: Context
) : ViewModel() {
    private val _loginResponseLiveData: MutableLiveData<Event<Resource<LoginResponse>>> = MutableLiveData()
    val loginResponseLiveData: LiveData<Event<Resource<LoginResponse>>>
        get() = _loginResponseLiveData

    private var loginResponse: LoginResponse? = null

    fun login(post: LoginPost) = viewModelScope.launch(Dispatchers.IO) {
        safeLogin(post)
    }

    private suspend fun safeLogin(post: LoginPost) {
        try {
            val response = mdmRepository.login(post)
            _loginResponseLiveData.postValue(Event(handleLoginResponse(response)))
        } catch (e: Exception){
            Log.e("LOGIN_API_ERROR", e.toString())
            _loginResponseLiveData.postValue(Event(Resource.Error(e.toString())))
        }

    }

    private fun handleLoginResponse(response: Response<LoginResponse>): Resource<LoginResponse> {
        if (response.isSuccessful) {
            Log.d("LOGIN_RETROFIT_SUCCESS", response.body()?.accessToken.toString())
            response.body()?.let { resultResponse ->
                return Resource.Success(loginResponse ?: resultResponse)
            }
        } else {
            Log.e("LOGIN_RETROFIT_ERROR", response.toString())
        }
        return Resource.Error((loginResponse ?: response.message()).toString())
    }

    suspend fun saveToken(accessToken: String) {
        mdmRepository.saveToken(accessToken)
    }

    suspend fun saveUserInfo(user: User) {
        mdmRepository.saveUserInfo(user)
    }

    suspend fun clearData() {
        mdmRepository.clearData()
    }
}