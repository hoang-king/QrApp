package com.example.qrgrenertor.di

import com.example.qrgrenertor.data.repository_Impl.QRCodeRepositoryImpl
import com.example.qrgrenertor.data.sync.SyncManager
import com.example.qrgrenertor.data.sync.SyncManagerImpl
import com.example.qrgrenertor.domain.repository.QRCodeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindQRCodeRepository(
        impl: QRCodeRepositoryImpl
    ): QRCodeRepository

    @Binds
    @Singleton
    abstract fun bindSyncManager(
        impl: SyncManagerImpl
    ): SyncManager
}
