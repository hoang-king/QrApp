package com.example.qrgrenertor.domain.repository



import android.graphics.Bitmap
import com.example.qrgrenertor.data.generator.QRCodeGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QRCodeRepository(
    private val qrCodeGenerator: QRCodeGenerator = QRCodeGenerator()
) {
    suspend fun generateQRCode(text: String): Bitmap? {
        return withContext(Dispatchers.Default) {
            qrCodeGenerator.generateQRCode(text)
        }
    }
}