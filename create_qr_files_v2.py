import os
from pathlib import Path

base_path = r"C:\Users\Nam_Nguyen\AndroidStudioProjects\QrApp1\app\src\main\java\com\example\qrgrenertor"

# Create all needed directories first
directories = [
    "domain\\model",
    "domain\\usecase",
    "data\\local\\entity",
    "data\\local\\dao",
    "data\\local\\database",
    "data\\remote\\dto",
    "data\\remote\\api",
    "data\\mapper",
    "data\\repository_impl",
    "data\\sync",
]

for directory in directories:
    dir_path = os.path.join(base_path, directory)
    os.makedirs(dir_path, exist_ok=True)
    print(f"✓ Created directory: {directory}")

# Domain Layer Files
domain_files = {
    "domain\\model\\QRCode.kt": '''package com.example.qrgrenertor.domain.model

data class QRCode(
    val id: String,
    val content: String,
    val sourceType: QRSourceType,
    val imageUrl: String,
    val createdAt: Long,
    val isSynced: Boolean = false
)

enum class QRSourceType {
    URL, PDF, IMAGE, FACEBOOK, INSTAGRAM, MUSIC, WIFI
}

data class QRSource(
    val type: QRSourceType,
    val value: String,
    val metadata: Map<String, String> = emptyMap()
)

data class QRHistory(
    val id: String,
    val qrCode: QRCode,
    val lastAccessedAt: Long
)

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}''',

    "domain\\usecase\\GenerateQRUseCase.kt": '''package com.example.qrgrenertor.domain.usecase

import com.example.qrgrenertor.domain.model.QRCode
import com.example.qrgrenertor.domain.model.Result
import com.example.qrgrenertor.domain.repository.QRCodeRepository

class GenerateQRUseCase(private val repository: QRCodeRepository) {
    suspend operator fun invoke(content: String): Result<QRCode> {
        return if (content.isEmpty()) {
            Result.Error(Exception("Content cannot be empty"))
        } else {
            repository.generateQR(content)
        }
    }
}''',

    "domain\\usecase\\SaveQRUseCase.kt": '''package com.example.qrgrenertor.domain.usecase

import com.example.qrgrenertor.domain.model.QRCode
import com.example.qrgrenertor.domain.model.Result
import com.example.qrgrenertor.domain.repository.QRCodeRepository

class SaveQRUseCase(private val repository: QRCodeRepository) {
    suspend operator fun invoke(qrCode: QRCode): Result<Unit> {
        return repository.saveQR(qrCode)
    }
}''',

    "domain\\usecase\\GetQRHistoryUseCase.kt": '''package com.example.qrgrenertor.domain.usecase

import com.example.qrgrenertor.domain.model.QRHistory
import com.example.qrgrenertor.domain.model.Result
import com.example.qrgrenertor.domain.repository.QRCodeRepository

class GetQRHistoryUseCase(private val repository: QRCodeRepository) {
    suspend operator fun invoke(): Result<List<QRHistory>> {
        return repository.getQRHistory()
    }
}''',
}

# Data Layer Files  
data_files = {
    "data\\local\\entity\\QRCodeEntity.kt": '''package com.example.qrgrenertor.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_codes")
data class QRCodeEntity(
    @PrimaryKey val id: String,
    val content: String,
    val sourceType: String,
    val imageUrl: String,
    val createdAt: Long,
    val isSynced: Boolean = false
)''',

    "data\\local\\dao\\QRCodeDao.kt": '''package com.example.qrgrenertor.data.local.dao

import androidx.room.*
import com.example.qrgrenertor.data.local.entity.QRCodeEntity

@Dao
interface QRCodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQRCode(qrCode: QRCodeEntity)

    @Query("SELECT * FROM qr_codes ORDER BY createdAt DESC")
    suspend fun getAllQRCodes(): List<QRCodeEntity>

    @Query("SELECT * FROM qr_codes WHERE id = :id")
    suspend fun getQRCodeById(id: String): QRCodeEntity?

    @Delete
    suspend fun deleteQRCode(qrCode: QRCodeEntity)

    @Query("DELETE FROM qr_codes WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE qr_codes SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}''',

    "data\\local\\database\\AppDatabase.kt": '''package com.example.qrgrenertor.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.qrgrenertor.data.local.dao.QRCodeDao
import com.example.qrgrenertor.data.local.entity.QRCodeEntity

@Database(
    entities = [QRCodeEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun qrCodeDao(): QRCodeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "qr_app_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}''',

    "data\\remote\\dto\\QRCodeDto.kt": '''package com.example.qrgrenertor.data.remote.dto

import com.google.gson.annotations.SerializedName

data class QRCodeDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("sourceType")
    val sourceType: String,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("createdAt")
    val createdAt: Long,
    @SerializedName("isSynced")
    val isSynced: Boolean = false
)''',

    "data\\remote\\api\\QRCodeApi.kt": '''package com.example.qrgrenertor.data.remote.api

import com.example.qrgrenertor.data.remote.dto.QRCodeDto
import retrofit2.http.*

interface QRCodeApi {
    @POST("/api/qr/generate")
    suspend fun generateQRCode(@Body request: GenerateQRRequest): QRCodeDto

    @GET("/api/qr/{id}")
    suspend fun getQRCode(@Path("id") id: String): QRCodeDto

    @POST("/api/qr/save")
    suspend fun saveQRCode(@Body qrCode: QRCodeDto): QRCodeDto

    @GET("/api/qr/sync")
    suspend fun syncQRCodes(): List<QRCodeDto>

    @DELETE("/api/qr/{id}")
    suspend fun deleteQRCode(@Path("id") id: String)
}

data class GenerateQRRequest(
    val content: String,
    val sourceType: String
)''',

    "data\\mapper\\QRCodeMapper.kt": '''package com.example.qrgrenertor.data.mapper

import com.example.qrgrenertor.data.local.entity.QRCodeEntity
import com.example.qrgrenertor.data.remote.dto.QRCodeDto
import com.example.qrgrenertor.domain.model.QRCode
import com.example.qrgrenertor.domain.model.QRSourceType

fun QRCodeEntity.toDomain() = QRCode(
    id = id,
    content = content,
    sourceType = QRSourceType.valueOf(sourceType),
    imageUrl = imageUrl,
    createdAt = createdAt,
    isSynced = isSynced
)

fun QRCodeDto.toDomain() = QRCode(
    id = id,
    content = content,
    sourceType = QRSourceType.valueOf(sourceType),
    imageUrl = imageUrl,
    createdAt = createdAt,
    isSynced = isSynced
)

fun QRCode.toEntity() = QRCodeEntity(
    id = id,
    content = content,
    sourceType = sourceType.name,
    imageUrl = imageUrl,
    createdAt = createdAt,
    isSynced = isSynced
)

fun QRCode.toDto() = QRCodeDto(
    id = id,
    content = content,
    sourceType = sourceType.name,
    imageUrl = imageUrl,
    createdAt = createdAt,
    isSynced = isSynced
)''',

    "data\\repository_impl\\QRCodeRepositoryImpl.kt": '''package com.example.qrgrenertor.data.repository_impl

import com.example.qrgrenertor.data.local.dao.QRCodeDao
import com.example.qrgrenertor.data.mapper.toDomain
import com.example.qrgrenertor.data.mapper.toEntity
import com.example.qrgrenertor.data.remote.api.QRCodeApi
import com.example.qrgrenertor.domain.model.QRCode
import com.example.qrgrenertor.domain.model.QRHistory
import com.example.qrgrenertor.domain.model.Result
import com.example.qrgrenertor.domain.repository.QRCodeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QRCodeRepositoryImpl(
    private val localDao: QRCodeDao,
    private val remoteApi: QRCodeApi,
    private val syncManager: SyncManager
) : QRCodeRepository {

    override suspend fun generateQR(content: String): Result<QRCode> = withContext(Dispatchers.IO) {
        return@withContext try {
            val dto = remoteApi.generateQRCode(
                com.example.qrgrenertor.data.remote.api.GenerateQRRequest(
                    content = content,
                    sourceType = "URL"
                )
            )
            Result.Success(dto.toDomain())
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
}''',

    "data\\sync\\SyncManagerImpl.kt": '''package com.example.qrgrenertor.data.sync

import com.example.qrgrenertor.data.local.dao.QRCodeDao
import com.example.qrgrenertor.data.remote.api.QRCodeApi
import com.example.qrgrenertor.data.repository_impl.SyncManager

class SyncManagerImpl(
    private val localDao: QRCodeDao,
    private val remoteApi: QRCodeApi
) : SyncManager {

    override suspend fun markForSync(id: String) {
        // Mark record as pending sync
        // Implementation: Store in sync queue table
    }

    override suspend fun syncAll() {
        try {
            val unsyncedCodes = localDao.getAllQRCodes().filter { !it.isSynced }
            
            for (entity in unsyncedCodes) {
                try {
                    remoteApi.saveQRCode(
                        com.example.qrgrenertor.data.remote.dto.QRCodeDto(
                            id = entity.id,
                            content = entity.content,
                            sourceType = entity.sourceType,
                            imageUrl = entity.imageUrl,
                            createdAt = entity.createdAt,
                            isSynced = true
                        )
                    )
                    localDao.markAsSynced(entity.id)
                } catch (e: Exception) {
                    // Log error and continue with next item
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}''',
}

# Create all files
all_files = {**domain_files, **data_files}

for file_path, content in all_files.items():
    full_path = os.path.join(base_path, file_path)
    with open(full_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"✓ Created: {file_path}")

print(f"\n✓ Successfully created {len(all_files)} files for QR Generator!")
print(f"\n✓ All {len(directories)} directories also created!")
print(f"\n✓ Total items created: {len(all_files) + len(directories)}")
