package com.applock.secure.di

import android.content.Context
import com.applock.secure.data.local.SecurePreferences
import com.applock.secure.domain.usecase.EncryptionHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for app-level dependencies
 * Provides singleton instances of common utilities
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSecurePreferences(
        @ApplicationContext context: Context
    ): SecurePreferences {
        return SecurePreferences(context)
    }

    @Provides
    @Singleton
    fun provideEncryptionHelper(): EncryptionHelper {
        return EncryptionHelper()
    }
}