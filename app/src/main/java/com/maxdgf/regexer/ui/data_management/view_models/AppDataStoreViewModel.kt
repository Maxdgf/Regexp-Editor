package com.maxdgf.regexer.ui.data_management.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxdgf.regexer.core.data_management.app_data_store.repository.AppDataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppDataStoreViewModel @Inject constructor(private val appDataStoreRepository: AppDataStoreRepository) : ViewModel() {
    val currentSelectionColor = appDataStoreRepository.getCurrentSelectionColor().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        0L
    )

    fun saveCurrentSelectionColor(color: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            appDataStoreRepository.saveCurrentSelectionColor(color)
        }
    }
}