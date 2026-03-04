package com.maxdgf.regexer.di

import android.content.Context
import androidx.room.Room
import com.maxdgf.regexer.core.data_management.app_data_store.repository.AppDataStoreRepository
import com.maxdgf.regexer.core.data_management.app_data_store.repository.AppDataStoreRepositoryImpl
import com.maxdgf.regexer.core.data_management.databases.saved_regexp_patterns_database.RegexpPatternDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

import com.maxdgf.regexer.core.data_management.databases.saved_regexp_patterns_database.RegexpPatternDatabase
import com.maxdgf.regexer.core.data_management.databases.saved_regexp_patterns_database.repository.RegexpPatternRepository
import com.maxdgf.regexer.core.data_management.databases.saved_regexp_patterns_database.repository.RegexpPatternRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideRegexpPatternAppDatabase(@ApplicationContext context: Context): RegexpPatternDatabase =
        Room.databaseBuilder(
            context,
            RegexpPatternDatabase::class.java,
            "regexp_database"
        ).fallbackToDestructiveMigration(false).build()

    @Provides
    fun regexpPatternDao(regexpPatternDatabase: RegexpPatternDatabase) =
        regexpPatternDatabase.getRegexpPatternDao()

    @Singleton
    @Provides
    fun provideRegexpPatternRepository(regexpPatternDao: RegexpPatternDao) : RegexpPatternRepository =
        RegexpPatternRepositoryImpl(regexpPatternDao)

    @Singleton
    @Provides
    fun provideAppDataStoreRepository(@ApplicationContext context: Context) : AppDataStoreRepository =
        AppDataStoreRepositoryImpl(context)
}