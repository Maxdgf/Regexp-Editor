package com.maxdgf.regexer.core.data_management.databases.saved_regexp_patterns_database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.maxdgf.regexer.core.data_management.databases.saved_regexp_patterns_database.entities.RegexpPatternEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RegexpPatternDao {
    @Query("SELECT * FROM saved_regexp_patterns") // get all patterns
    fun getAllSavedRegexpPatterns(): Flow<List<RegexpPatternEntity>>

    @Insert(onConflict = REPLACE) // add pattern
    suspend fun addRegexpPattern(regexp: RegexpPatternEntity)

    @Query("DELETE FROM saved_regexp_patterns WHERE regexp_uuid = :uuid") // delete current pattern by id
    suspend fun deleteRegexpPatternByUuid(uuid: String)

    @Query("DELETE FROM saved_regexp_patterns") //delete all patterns
    suspend fun deleteAllSavedRegexpPatterns()
}