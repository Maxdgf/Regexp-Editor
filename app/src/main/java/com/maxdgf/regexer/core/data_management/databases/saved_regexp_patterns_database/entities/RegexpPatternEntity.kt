package com.maxdgf.regexer.core.data_management.databases.saved_regexp_patterns_database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_regexp_patterns")
data class RegexpPatternEntity(
    @PrimaryKey(autoGenerate = true) val regexpId: Long = 0,                  // num id
    @ColumnInfo(name = "regexp_name") val name: String,                      // name
    @ColumnInfo(name = "regexp_string") val regexpString: String,            // pattern string
    @ColumnInfo(name = "is_global_search") val isGlobalSearchState: Boolean, // global search state
    @ColumnInfo(name = "flags") val flags: String,                           // regexp flags
)