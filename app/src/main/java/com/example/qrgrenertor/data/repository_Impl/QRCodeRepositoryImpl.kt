package com.example.qrgrenertor.data.repository_impl

import com.example.qrgrenertor.data.local.QRCodeDao
import com.example.qrgrenertor.data.mapper.toDomain
import com.example.qrgrenertor.data.mapper.toEntity
import com.example.qrgrenertor.data.remote.QRCodeApi
import com.example.qrgrenertor.domain.model.QRCode
import com.example.qrgrenertor.domain.model.QRHistory
import com.example.qrgrenertor.domain.model.Result
import com.example.qrgrenertor.domain.repository.QRCodeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class QRCodeRepositoryImpl @Inject constructor(
    private val localDao: QRCodeDao,
    private val remoteApi: QRCodeApi,
    private val syncManager: SyncManager
) : QRCodeRepository {

    override suspend fun generateQR(content: String): Result<QRCode> = withContext(Dispatchers.IO) {
        return@withContext try {
            val qrCode = QRCode(
                id = UUID.randomUUID().toString(),
                content = content,
                sourceType = com.example.qrgrenertor.domain.model.QRSourceType.URL,
                designSettings = com.example.qrgrenertor.domain.model.QRDesign(),
                imageUrl = null,
                createdAt = System.currentTimeMillis()
            )
            Result.Success(qrCode)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun saveQR(qrCode: QRCode): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            localDao.insertQRCode(qrCode.toEntity())
            syncManager.markForSync(qrCode.id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getQRHistory(): Result<List<QRHistory>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val entities = localDao.getAllQRCodes()
            val history = entities.map { entity ->
                QRHistory(
                    id = entity.id,
                    qrCode = entity.toDomain(),
                    lastAccessedAt = System.currentTimeMillis()
                )
            }
            Result.Success(history)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getQRById(id: String): Result<QRCode> = withContext(Dispatchers.IO) {
        return@withContext try {
            val entity = localDao.getQRCodeById(id)
            if (entity != null) {
                Result.Success(entity.toDomain())
            } else {
                Result.Error(Exception("QR Code not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteQR(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            localDao.deleteById(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun syncData(): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            syncManager.syncAll()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

interface SyncManager {
    suspend fun markForSync(id: String)
    suspend fun syncAll()
}
