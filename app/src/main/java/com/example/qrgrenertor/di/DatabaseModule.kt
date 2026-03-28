package com.example.qrgrenertor.di

import android.content.Context
import com.example.qrgrenertor.data.local.AppDatabase
import com.example.qrgrenertor.data.local.QRCodeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideQRCodeDao(database: AppDatabase): QRCodeDao {
        return database.qrCodeDao()
    }
}
