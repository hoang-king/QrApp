package com.example.qrgrenertor.data.local

import androidx.room.*
import com.example.qrgrenertor.data.local.entity.QRCodeEntity

@Dao
interface QRCodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQRCode(qrCode: QRCodeEntity): Unit

    @Query("SELECT * FROM qr_codes ORDER BY createdAt DESC")
    suspend fun getAllQRCodes(): List<QRCodeEntity>

    @Query("SELECT * FROM qr_codes WHERE id = :id")
    suspend fun getQRCodeById(id: String): QRCodeEntity?

    @Delete
    suspend fun deleteQRCode(qrCode: QRCodeEntity): Unit

    @Query("DELETE FROM qr_codes WHERE id = :id")
    suspend fun deleteById(id: String): Unit

    @Query("UPDATE qr_codes SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String): Unit
}
