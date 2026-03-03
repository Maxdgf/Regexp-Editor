package com.maxdgf.regexer.core.data_management.app_data_store.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore by preferencesDataStore(name = "Regexer_preferences")

class AppDataStoreRepositoryImpl @Inject constructor(private val context: Context) : AppDataStoreRepository {
    companion object {
        val currentSelectionMatchesColor = longPreferencesKey(name = "current_selection_matches_color")
    }

    override fun getCurrentSelectionColor(): Flow<Long> = context.dataStore.data.map {
        it[currentSelectionMatchesColor] ?: -1099511627776 // yellow long
    }

    override suspend fun saveCurrentSelectionColor(color: Long) {
        context.dataStore.edit {
            it[currentSelectionMatchesColor] = color
        }
    }
}