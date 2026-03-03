package com.maxdgf.regexer.ui.data_management.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.maxdgf.regexer.core.data_management.databases.saved_regexp_patterns_database.entities.RegexpPatternEntity
import com.maxdgf.regexer.core.data_management.databases.saved_regexp_patterns_database.repository.RegexpPatternRepository

@HiltViewModel
class SavedRegexpPatternsState @Inject constructor(private val regexpPatternRepository: RegexpPatternRepository) : ViewModel() {
    val savedRegexpPatternsList = regexpPatternRepository.getAllSavedRegexpPatterns().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    /**
     * Writes new regexp pattern item to db.
     * @param regexp regexp entity.
     */
    fun addRegexp(regexp: RegexpPatternEntity) {
        viewModelScope.launch {
            regexpPatternRepository.addRegexpPattern(regexp)
        }
    }

    /**
     * Deletes regexp pattern item from db.
     * @param uuid regexp item uuid.
     */
    fun deleteRegexpByUuid(uuid: String) {
        viewModelScope.launch {
            regexpPatternRepository.deleteRegexpPatternByUuid(uuid)
        }
    }

    /**Deletes all regexp pattern items from db.*/
    fun deleteAllRegexpPatterns() {
        viewModelScope.launch {
            regexpPatternRepository.deleteAllSavedRegexpPatterns()
        }
    }
}