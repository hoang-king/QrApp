package com.example.qrgrenertor

import android.content.Context
import com.example.qrgrenertor.data.local.AppDatabase
import com.example.qrgrenertor.data.local.QRCodeDao
import com.example.qrgrenertor.data.remote.QRCodeApi
import com.example.qrgrenertor.data.repository_impl.QRCodeRepositoryImpl
import com.example.qrgrenertor.data.repository_impl.SyncManager
import com.example.qrgrenertor.data.sync.SyncManagerImpl
import com.example.qrgrenertor.domain.repository.QRCodeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.example.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideQRCodeApi(retrofit: Retrofit): QRCodeApi {
        return retrofit.create(QRCodeApi::class.java)
    }

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

    @Provides
    @Singleton
    fun provideSyncManager(
        dao: QRCodeDao,
        api: QRCodeApi
    ): SyncManager {
        return SyncManagerImpl(dao, api)
    }

    @Provides
    @Singleton
    fun provideQRCodeRepository(
        dao: QRCodeDao,
        api: QRCodeApi,
        syncManager: SyncManager
    ): QRCodeRepository {
        return QRCodeRepositoryImpl(dao, api, syncManager)
    }
}
