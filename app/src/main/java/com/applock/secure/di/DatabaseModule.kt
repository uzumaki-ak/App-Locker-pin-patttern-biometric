package com.applock.secure.di

import android.content.Context
import androidx.room.Room
import com.applock.secure.data.local.AppDatabase
import com.applock.secure.data.local.LockedAppDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for Room Database
 * Provides database and DAO instances
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()  // For now, recreate on schema change
            .build()
    }

    @Provides
    @Singleton
    fun provideLockedAppDao(database: AppDatabase): LockedAppDao {
        return database.lockedAppDao()
    }
}