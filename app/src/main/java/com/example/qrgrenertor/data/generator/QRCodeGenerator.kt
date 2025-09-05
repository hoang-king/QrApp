package com.example.qrgrenertor.data.generator

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat

import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.common.BitMatrix
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set


class QRCodeGenerator {

    fun generateQRCode(text: String, size: Int = 512): Bitmap? {
        return try {
            val bitMatrix: BitMatrix = QRCodeWriter().encode(
                text,
                BarcodeFormat.QR_CODE,
                size,
                size
            )

            val bitmap = createBitmap(size, size, Bitmap.Config.ARGB_8888)

            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                }
            }

            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}