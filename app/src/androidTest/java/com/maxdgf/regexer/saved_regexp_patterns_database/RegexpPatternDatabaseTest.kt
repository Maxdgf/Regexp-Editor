package com.maxdgf.regexer.saved_regexp_patterns_database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import java.io.IOException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test

import com.maxdgf.regexer.core.data_management.databases.saved_regexp_patterns_database.RegexpPatternDao
import com.maxdgf.regexer.core.data_management.databases.saved_regexp_patterns_database.RegexpPatternDatabase
import com.maxdgf.regexer.core.data_management.databases.saved_regexp_patterns_database.entities.RegexpPatternEntity

@RunWith(AndroidJUnit4::class)
class RegexpPatternDatabaseTest : TestCase() {
    private lateinit var regexpPatternDao: RegexpPatternDao
    private lateinit var db: RegexpPatternDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(context, RegexpPatternDatabase::class.java).build()
        regexpPatternDao = db.getRegexpPatternDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() =
        db.close()

    @Test
    @Throws(Exception::class)
    fun addRegexpPatternToDatabase() = runBlocking {
       val pattern = RegexpPatternEntity(
           name = "test regexp pattern",
           regexpString = "pattern",
           isGlobalSearchState = true,
           flags = "pattern flags"
       )

        regexpPatternDao.addRegexpPattern(pattern)
        val result = regexpPatternDao.getAllSavedRegexpPatterns().first()

        assertTrue(result.isNotEmpty())
        assertTrue(
            result.any { it.name == pattern.name && it.regexpString == pattern.regexpString }
        )
    }

    @Test
    @Throws(Exception::class)
    fun deleteAllPatternsFromDatabase() = runBlocking {
        val pattern1 = RegexpPatternEntity(
            name = "test regexp pattern 1",
            regexpString = "pattern 1",
            isGlobalSearchState = true,
            flags = "pattern flags"
        )
        val pattern2 = RegexpPatternEntity(
            name = "test regexp pattern 2",
            regexpString = "pattern 2",
            isGlobalSearchState = true,
            flags = "pattern flags"
        )

        regexpPatternDao.apply {
            addRegexpPattern(pattern1)
            addRegexpPattern(pattern2)
            deleteAllSavedRegexpPatterns()
        }

        val result = regexpPatternDao.getAllSavedRegexpPatterns().first()
        assertTrue(result.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun deletePatternByIdFromDatabase() = runBlocking {
        val pattern1 = RegexpPatternEntity(
            name = "test regexp pattern 1",
            regexpString = "pattern 1",
            isGlobalSearchState = true,
            flags = "pattern flags"
        )
        val pattern2 = RegexpPatternEntity(
            name = "test regexp pattern 2",
            regexpString = "pattern 2",
            isGlobalSearchState = true,
            flags = "pattern flags"
        )
        val pattern3 = RegexpPatternEntity(
            name = "test regexp pattern 3",
            regexpString = "pattern 3",
            isGlobalSearchState = false,
            flags = "pattern flags"
        )

        regexpPatternDao.apply {
            addRegexpPattern(pattern1)
            addRegexpPattern(pattern2)
            addRegexpPattern(pattern3)
            deleteRegexpPatternByUuid(2)
        }

        val result = regexpPatternDao.getAllSavedRegexpPatterns().first()
        assertTrue(!result.contains(pattern2))
    }
}