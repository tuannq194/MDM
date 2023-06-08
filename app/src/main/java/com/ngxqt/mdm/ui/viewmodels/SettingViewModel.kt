package com.ngxqt.mdm.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngxqt.mdm.data.model.User
import com.ngxqt.mdm.repository.MDMRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val mdmRepository: MDMRepository, @ApplicationContext private val context: Context
) : ViewModel() {
    fun saveSettingPassword(isSaved: Boolean) {
        viewModelScope.launch {
            mdmRepository.saveSettingPassword(isSaved)
        }
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

    fun clearData() {
        viewModelScope.launch {
            mdmRepository.clearData()
        }
    }
}