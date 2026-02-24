package com.example.qrgrenertor.domain.usecase

import com.example.qrgrenertor.domain.model.QRCode
import com.example.qrgrenertor.domain.model.Result
import com.example.qrgrenertor.domain.repository.QRCodeRepository
import javax.inject.Inject

class SaveQRUseCase @Inject constructor(
    private val repository: QRCodeRepository
) {
    suspend operator fun invoke(qrCode: QRCode): Result<Unit> {
        return repository.saveQR(qrCode)
    }
}
