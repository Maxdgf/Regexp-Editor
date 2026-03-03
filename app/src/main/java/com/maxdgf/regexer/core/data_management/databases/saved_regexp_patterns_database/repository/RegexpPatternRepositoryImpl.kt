package com.maxdgf.regexer.core.data_management.databases.saved_regexp_patterns_database.repository

import com.maxdgf.regexer.core.data_management.databases.saved_regexp_patterns_database.RegexpPatternDao
import com.maxdgf.regexer.core.data_management.databases.saved_regexp_patterns_database.entities.RegexpPatternEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegexpPatternRepositoryImpl @Inject constructor(private val regexpDao: RegexpPatternDao) : RegexpPatternRepository{
    override fun getAllSavedRegexpPatterns(): Flow<List<RegexpPatternEntity>> = regexpDao.getAllSavedRegexpPatterns()

    override suspend fun addRegexpPattern(regexp: RegexpPatternEntity) = regexpDao.addRegexpPattern(regexp)

    override suspend fun deleteRegexpPatternByUuid(uuid: String) = regexpDao.deleteRegexpPatternByUuid(uuid)

    override suspend fun deleteAllSavedRegexpPatterns() = regexpDao.deleteAllSavedRegexpPatterns()
}