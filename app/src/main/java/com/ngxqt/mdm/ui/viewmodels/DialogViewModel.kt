package com.ngxqt.mdm.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ngxqt.mdm.data.model.objectmodel.Equipment
import com.ngxqt.mdm.repository.MDMRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class DialogViewModel @Inject constructor(
    private val mdmRepository: MDMRepository, @ApplicationContext private val context: Context
) : ViewModel() {
    /**GET INVENTORY HISTORY*/
    fun getInventoryHistory(
        authorization: String,
        equipmentId: Int?
    ): LiveData<PagingData<Equipment>>{
        return mdmRepository.getInventoryHistory(
            authorization,
            equipmentId
        ).cachedIn(viewModelScope)
    }

    fun getRepairHistory(
        authorization: String,
        equipmentId: Int?
    ): LiveData<PagingData<Equipment>>{
        return mdmRepository.getRepairHistory(
            authorization,
            equipmentId
        ).cachedIn(viewModelScope)
    }
}