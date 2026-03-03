package com.maxdgf.regexer.core.data_management.databases.saved_regexp_patterns_database.repository

import com.maxdgf.regexer.core.data_management.databases.saved_regexp_patterns_database.entities.RegexpPatternEntity
import kotlinx.coroutines.flow.Flow

interface RegexpPatternRepository {
    fun getAllSavedRegexpPatterns(): Flow<List<RegexpPatternEntity>>
    suspend fun addRegexpPattern(regexp: RegexpPatternEntity)
    suspend fun deleteRegexpPatternByUuid(uuid: String)
    suspend fun deleteAllSavedRegexpPatterns()
}