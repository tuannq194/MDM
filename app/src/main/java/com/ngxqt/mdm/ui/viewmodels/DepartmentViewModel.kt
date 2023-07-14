package com.ngxqt.mdm.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ngxqt.mdm.data.model.Department
import com.ngxqt.mdm.repository.MDMRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class DepartmentViewModel @Inject constructor(
    private val mdmRepository: MDMRepository, @ApplicationContext private val context: Context
) : ViewModel() {
    fun getDepartments(
        authorization: String,
        keyword: String? = null
    ): LiveData<PagingData<Department>>{
        return mdmRepository.getDepartments(
            authorization, keyword
        ).cachedIn(viewModelScope)
    }
}