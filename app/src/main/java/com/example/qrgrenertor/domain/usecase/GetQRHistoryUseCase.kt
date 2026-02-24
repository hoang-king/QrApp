package com.example.qrgrenertor.domain.usecase

import com.example.qrgrenertor.domain.model.QRHistory
import com.example.qrgrenertor.domain.model.Result
import com.example.qrgrenertor.domain.repository.QRCodeRepository
import javax.inject.Inject

class GetQRHistoryUseCase @Inject constructor(
    private val repository: QRCodeRepository
) {
    suspend operator fun invoke(): Result<List<QRHistory>> {
        return repository.getQRHistory()
    }
}
