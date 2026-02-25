package com.example.qrgrenertor.domain.repository

import com.example.qrgrenertor.domain.model.QRCode
import com.example.qrgrenertor.domain.model.QRDesign
import com.example.qrgrenertor.domain.model.QRHistory
import com.example.qrgrenertor.domain.model.QRSourceType
import com.example.qrgrenertor.domain.model.Result

interface QRCodeRepository {
    suspend fun generateQR(name: String, content: String, type: QRSourceType, design: QRDesign): Result<QRCode>
    suspend fun saveQR(qrCode: QRCode): Result<Unit>
    suspend fun getQRHistory(): Result<List<QRHistory>>
    suspend fun getQRById(id: String): Result<QRCode>
    suspend fun deleteQR(id: String): Result<Unit>
    suspend fun syncData(): Result<Unit>
}
