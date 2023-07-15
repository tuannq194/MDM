package com.ngxqt.mdm.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ngxqt.mdm.data.model.objectmodel.User
import com.ngxqt.mdm.repository.MDMRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class StaffViewModel @Inject constructor(
    private val mdmRepository: MDMRepository, @ApplicationContext private val context: Context
) : ViewModel() {
    fun getUsers(
        authorization: String, keyword: String?,
        roleId: Int?, departmentId: Int?
    ): LiveData<PagingData<User>>{
        return mdmRepository.getUsers(
            authorization, keyword, roleId, departmentId
        ).cachedIn(viewModelScope)
    }
}