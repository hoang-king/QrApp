package com.example.qrgrenertor.presentation.ui

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.example.qrgrenertor.domain.model.QRDesign
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

object QRGeneratorUtils {
    fun generateQRCode(
        content: String,
        design: QRDesign
    ): Bitmap {
        val size = design.size
        val hints = HashMap<EncodeHintType, Any>()
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.ERROR_CORRECTION] = when (design.errorCorrectionLevel) {
            com.example.qrgrenertor.domain.model.ErrorCorrectionLevel.LOW -> ErrorCorrectionLevel.L
            com.example.qrgrenertor.domain.model.ErrorCorrectionLevel.MEDIUM -> ErrorCorrectionLevel.M
            com.example.qrgrenertor.domain.model.ErrorCorrectionLevel.QUARTILE -> ErrorCorrectionLevel.Q
            com.example.qrgrenertor.domain.model.ErrorCorrectionLevel.HIGH -> ErrorCorrectionLevel.H
        }
        hints[EncodeHintType.MARGIN] = 1

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) design.codeColor.toInt() else design.backgroundColor.toInt())
            }
        }
        return bitmap
    }
}
