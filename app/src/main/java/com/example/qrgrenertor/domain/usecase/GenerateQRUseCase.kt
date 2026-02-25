package com.example.qrgrenertor.domain.usecase

import com.example.qrgrenertor.domain.model.QRCode
import com.example.qrgrenertor.domain.model.QRDesign
import com.example.qrgrenertor.domain.model.QRSourceType
import com.example.qrgrenertor.domain.model.Result
import com.example.qrgrenertor.domain.repository.QRCodeRepository
import javax.inject.Inject

class GenerateQRUseCase @Inject constructor(
    private val repository: QRCodeRepository
) {
    suspend operator fun invoke(name: String, content: String, type: QRSourceType, design: QRDesign): Result<QRCode> {
        return if (content.isEmpty()) {
            Result.Error(Exception("Content cannot be empty"))
        } else {
            repository.generateQR(name, content, type, design)
        }
    }
}
