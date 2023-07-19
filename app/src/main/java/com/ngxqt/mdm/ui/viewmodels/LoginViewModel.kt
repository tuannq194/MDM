package com.ngxqt.mdm.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngxqt.mdm.data.model.objectmodel.User
import com.ngxqt.mdm.data.model.postmodel.LoginPost
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
class LoginViewModel @Inject constructor(
    private val mdmRepository: MDMRepository, @ApplicationContext private val context: Context
) : ViewModel() {
    private val _loginResponseLiveData: MutableLiveData<Event<Resource<HostResponse>>> = MutableLiveData()
    val loginResponseLiveData: LiveData<Event<Resource<HostResponse>>>
        get() = _loginResponseLiveData

    private var loginResponse: HostResponse? = null

    fun login(post: LoginPost) = viewModelScope.launch() {
        safeLogin(post)
    }

    private suspend fun safeLogin(post: LoginPost) {
        _loginResponseLiveData.postValue(Event(Resource.Loading()))
        try {
            if(hasInternetConnection(context)){
                val response = mdmRepository.login(post)
                _loginResponseLiveData.postValue(Event(handleLoginResponse(response)))
            }else{
                _loginResponseLiveData.postValue(Event(Resource.Error("Mất Kết Nối Internet")))
            }
        } catch (e: Exception) {
            LogUtils.d("LOGIN_API_ERROR: ${e.message}")
            _loginResponseLiveData.postValue(Event(Resource.Error("${e.message.toString()}")))
        }
    }

    private fun handleLoginResponse(response: Response<HostResponse>): Resource<HostResponse> {
        if (response.isSuccessful) {
            LogUtils.d("LOGIN_RETROFIT_SUCCESS: OK")
            response.body()?.let { resultResponse ->
                return Resource.Success(loginResponse ?: resultResponse)
            }
        } else {
            LogUtils.d("LOGIN_RETROFIT_ERROR: $response")
            var res = response.body()?.message.toString()
            if (response.code()==401) res = "Email Hoặc Mật Khẩu Không Đúng"
            else if (response.code()==400) res = "Invalid request body"
            else if (response.code()==500) res = "Internal server error"
            return Resource.Error(res)
        }
        return Resource.Error((loginResponse ?: response.message()).toString())
    }

    fun saveToken(accessToken: String) {
        viewModelScope.launch {
            mdmRepository.saveToken(accessToken)
        }
    }

    fun saveUserInfo(user: User) {
        viewModelScope.launch {
            mdmRepository.saveUserInfo(user)
        }
    }
}