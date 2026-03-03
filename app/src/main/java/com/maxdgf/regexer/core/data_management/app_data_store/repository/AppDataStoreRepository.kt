package com.maxdgf.regexer.core.data_management.app_data_store.repository

import kotlinx.coroutines.flow.Flow

interface AppDataStoreRepository {
    fun getCurrentSelectionColor(): Flow<Long>
    suspend fun saveCurrentSelectionColor(color: Long)
}